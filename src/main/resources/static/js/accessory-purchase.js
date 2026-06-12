/**
 * accessory-purchase.js
 * Handles CREATE and EDIT for all three accessory types: Cone, Size Pattern, Others.
 * Edit mode: accessory-purchase.html?id=123&type=CONE (or SIZE_PATTERN / OTHERS)
 */

const ACCESSORY_TYPES = ['Cone', 'Size Pattern', 'Others'];

let coneRowCount = 0;
let editId   = null;
let editType = null;

document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    editId   = getQueryParam('id');
    editType = getQueryParam('type');    // CONE | SIZE_PATTERN | OTHERS
    const isEdit = !!editId;

    document.getElementById('page-heading').textContent = isEdit ? 'Edit Accessory Purchase' : 'Accessories';

    // Populate accessory type dropdown
    const typeSelect = document.getElementById('accessory-type-select');
    ACCESSORY_TYPES.forEach(t => {
        const o = document.createElement('option');
        o.value = t.toUpperCase().replace(' ', '_');
        o.textContent = t;
        typeSelect.appendChild(o);
    });

    typeSelect.addEventListener('change', () => {
        showSection(typeSelect.value);
    });

    document.getElementById('add-cone-row-btn').addEventListener('click', () => addConeRow());
    document.getElementById('go-back-btn').addEventListener('click', goBack);

    // Auto-calc for cone rows (delegated)
    document.getElementById('cone-rows').addEventListener('input', () => calcConeTotal());
    // Auto-calc for size pattern & others price
    document.getElementById('sp-price').addEventListener('input', () => {
        document.getElementById('sp-total-display').textContent = formatCurrency(document.getElementById('sp-price').value || 0);
    });
    document.getElementById('others-price').addEventListener('input', () => {
        document.getElementById('others-total-display').textContent = formatCurrency(document.getElementById('others-price').value || 0);
    });

    document.getElementById('accessory-form').addEventListener('submit', handleSubmit);

    if (isEdit && editType) {
        const sel = typeSelect;
        sel.value = editType;
        sel.disabled = true;   // type-locked in edit mode
        showSection(editType);
        await loadExistingAccessory(editId, editType);
    } else {
        addConeRow(); // default first cone row pre-created (hidden until selected)
    }

});

function showSection(type) {
    document.querySelectorAll('.accessory-section').forEach(s => s.classList.remove('active'));
    const sectionMap = { 'CONE': 'cone-section', 'SIZE_PATTERN': 'sp-section', 'OTHERS': 'others-section' };
    const target = sectionMap[type];
    if (target) document.getElementById(target).classList.add('active');
}

// ── Cone Rows ────────────────────────────────────────────────────────────────

function addConeRow(data = {}) {
    coneRowCount++;
    const idx = coneRowCount;
    const container = document.getElementById('cone-rows');
    const div = document.createElement('div');
    div.className = 'item-row-group';
    div.id = `cone-row-${idx}`;
    div.innerHTML = `
      <div class="row-header">
        <span class="row-title">Cone Entry #${idx}</span>
        ${idx > 1 ? `<button type="button" class="remove-row-btn" onclick="removeConeRow(${idx})" title="Remove">✕</button>` : ''}
      </div>
      <div class="form-group">
        <label class="form-label">Colour Name</label>
        <input type="text" class="form-control" name="cone_colourName_${idx}" placeholder="Enter colour name" required value="${esc(data.colourName || '')}">
      </div>
      <div class="form-group">
        <label class="form-label">Colour Code</label>
        <input type="text" class="form-control" name="cone_colourCode_${idx}" placeholder="Enter colour code" required value="${esc(data.colourCode || '')}">
      </div>
      <div class="form-group">
        <label class="form-label">Unit</label>
        <input type="number" class="form-control" name="cone_unit_${idx}" placeholder="Enter unit" step="0.01" min="0.01" required value="${esc(data.unit || '')}" oninput="calcConeTotal()">
      </div>
      <div class="form-group">
        <label class="form-label">Price per Unit</label>
        <div class="input-unit-wrap">
          <span class="unit-label" style="border-radius:var(--radius-sm) 0 0 var(--radius-sm); border-right:none; border-left:1px solid var(--border);">₹</span>
          <input type="number" class="form-control" name="cone_pricePerUnit_${idx}" placeholder="Enter price" step="0.01" min="0.01" required value="${esc(data.pricePerUnit || '')}" oninput="calcConeTotal()" style="border-radius:0 var(--radius-sm) var(--radius-sm) 0;">
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">Row Total Price</label>
        <div class="calc-display">
          <span class="currency-symbol">₹</span>
          <input type="text" class="form-control" id="cone-rowTotal_${idx}" readonly placeholder="Auto-calculated" value="${esc(data.rowTotal || '')}">
        </div>
      </div>`;
    container.appendChild(div);
    if (data.unit && data.pricePerUnit) calcConeTotal();
}

function removeConeRow(idx) {
    const el = document.getElementById(`cone-row-${idx}`);
    if (el) { el.remove(); calcConeTotal(); }
}

function calcConeTotal() {
    let total = 0;
    document.querySelectorAll('[id^="cone-row-"]').forEach(row => {
        const idx = row.id.replace('cone-row-', '');
        const u = parseFloat(document.querySelector(`[name="cone_unit_${idx}"]`)?.value) || 0;
        const p = parseFloat(document.querySelector(`[name="cone_pricePerUnit_${idx}"]`)?.value) || 0;
        const rt = u * p;
        const rtEl = document.getElementById(`cone-rowTotal_${idx}`);
        if (rtEl) rtEl.value = rt.toFixed(2);
        total += rt;
    });
    document.getElementById('cone-total-display').textContent = formatCurrency(total);
}

// ── Load Existing (Edit Mode) ─────────────────────────────────────────────────

async function loadExistingAccessory(id, type) {
    try {
        const res = await apiCall('GET', `/api/purchases/accessories/${id}`);
        const a   = res.data;

        document.getElementById('accessory-name-input').value = a.accessoryName || '';

        if (type === 'CONE' && a.cone) {
            // Clear default row, recreate from data
            document.getElementById('cone-rows').innerHTML = '';
            coneRowCount = 0;
            (a.cone.coneItems || []).forEach(item => addConeRow(item));
            if (!a.cone.coneItems || a.cone.coneItems.length === 0) addConeRow();
            calcConeTotal();
        } else if (type === 'SIZE_PATTERN' && a.sizePattern) {
            const sp = a.sizePattern;
            document.getElementById('sp-brand').value       = sp.brandName || '';
            document.getElementById('sp-style-number').value = sp.styleNumber || '';
            document.getElementById('sp-style-name').value  = sp.styleName || '';
            document.getElementById('sp-price').value       = sp.price || '';
            document.getElementById('sp-total-display').textContent = formatCurrency(sp.price);
        } else if (type === 'OTHERS' && a.others) {
            const o = a.others;
            document.getElementById('others-name').value  = o.itemsName || '';
            document.getElementById('others-unit').value  = o.unit || '';
            document.getElementById('others-price').value = o.price || '';
            document.getElementById('others-total-display').textContent = formatCurrency(o.price);
        }
    } catch (ex) {
        showToast('Failed to load accessory data: ' + ex.message, 'error');
    }
}

// ── Submit ────────────────────────────────────────────────────────────────────

async function handleSubmit(e) {
    e.preventDefault();
    const btn  = document.getElementById('submit-btn');
    const type = document.getElementById('accessory-type-select').value;
    const accName = document.getElementById('accessory-name-input').value.trim();
    btn.disabled = true;
    btn.textContent = editId ? 'Updating…' : 'Submitting…';

    try {
        let url, payload;

        if (type === 'CONE') {
            const rows = document.querySelectorAll('[id^="cone-row-"]');
            const coneItems = Array.from(rows).map(row => {
                const idx = row.id.replace('cone-row-', '');
                return {
                    colourName:   row.querySelector(`[name="cone_colourName_${idx}"]`).value.trim(),
                    colourCode:   row.querySelector(`[name="cone_colourCode_${idx}"]`).value.trim(),
                    unit:         parseFloat(row.querySelector(`[name="cone_unit_${idx}"]`).value),
                    pricePerUnit: parseFloat(row.querySelector(`[name="cone_pricePerUnit_${idx}"]`).value)
                };
            });
            payload = { accessoryName: accName, coneItems };
            url = editId ? `/api/purchases/accessories/cone/${editId}` : '/api/purchases/accessories/cone';
        } else if (type === 'SIZE_PATTERN') {
            payload = {
                accessoryName: accName,
                brandName:   document.getElementById('sp-brand').value.trim(),
                styleNumber: parseInt(document.getElementById('sp-style-number').value, 10),
                styleName:   document.getElementById('sp-style-name').value.trim(),
                price:       parseFloat(document.getElementById('sp-price').value)
            };
            url = editId ? `/api/purchases/accessories/size-pattern/${editId}` : '/api/purchases/accessories/size-pattern';
        } else if (type === 'OTHERS') {
            payload = {
                accessoryName: accName,
                itemsName: document.getElementById('others-name').value.trim(),
                unit:      parseFloat(document.getElementById('others-unit').value),
                price:     parseFloat(document.getElementById('others-price').value)
            };
            url = editId ? `/api/purchases/accessories/others/${editId}` : '/api/purchases/accessories/others';
        } else {
            showToast('Please select an accessory type.', 'error');
            btn.disabled = false;
            btn.textContent = editId ? 'Update' : 'Submit';
            return;
        }

        const method = editId ? 'PUT' : 'POST';
        await apiCall(method, url, payload);
        showToast(editId ? 'Accessory purchase updated!' : 'Accessory purchase saved!', 'success');
        setTimeout(() => {
            window.location.href = editId ? '/pages/accessory-history.html' : '/pages/dashboard.html';
        }, 1000);
    } catch (ex) {
        showToast(ex.message, 'error');
        btn.disabled = false;
        btn.textContent = editId ? 'Update' : 'Submit';
    }
}

function esc(v) {
    return String(v ?? '').replace(/"/g, '&quot;');
}
