/**
 * fabric-purchase.js
 * Handles both CREATE and EDIT modes for Fabric purchase form.
 * Edit mode: fabric-purchase.html?id=123
 */

// Hardcoded dropdown values (per Requirements.pdf wireframe)
const FABRIC_NAMES = ['Belui Fabric', 'Imayam Fabric', 'Surplus', 'Others'];
const FABRIC_TYPES = ['Loop Knit', 'Single Jersey', 'Interlock', 'Honey Comb', 'Salena', 'Others'];

let fabricRowCount = 0;
let editId = null;

document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    editId = getQueryParam('id');
    const isEdit = !!editId;

    document.getElementById('page-heading').textContent  = isEdit ? 'Edit Fabric Purchase' : 'Fabrics';
    document.getElementById('submit-btn').textContent    = isEdit ? 'Update' : 'Submit';

    populateDropdown('fabric-name-select', FABRIC_NAMES);
    populateDropdown('fabric-type-select', FABRIC_TYPES);

    setupDropdownOthers('fabric-name-select', 'fabric-name-manual-wrap', 'fabric-name-manual');
    setupDropdownOthers('fabric-type-select', 'fabric-type-manual-wrap', 'fabric-type-manual');

    document.getElementById('add-row-btn').addEventListener('click', () => addFabricRow());

    if (isEdit) {
        await loadExistingFabric(editId);
    } else {
        addFabricRow(); // start with one blank row
    }

    document.getElementById('fabric-form').addEventListener('submit', handleSubmit);
    document.getElementById('go-back-btn').addEventListener('click', goBack);
});

function populateDropdown(selectId, options) {
    const sel = document.getElementById(selectId);
    sel.innerHTML = `<option value="">-- Select --</option>`;
    options.forEach(opt => {
        const o = document.createElement('option');
        o.value = opt;
        o.textContent = opt;
        sel.appendChild(o);
    });
}

function setupDropdownOthers(selectId, wrapId, inputId) {
    document.getElementById(selectId).addEventListener('change', function () {
        const wrap = document.getElementById(wrapId);
        const inp  = document.getElementById(inputId);
        if (this.value === 'Others') {
            wrap.classList.add('show');
            inp.required = true;
        } else {
            wrap.classList.remove('show');
            inp.required = false;
            inp.value = '';
        }
    });
}

function addFabricRow(data = {}) {
    fabricRowCount++;
    const idx = fabricRowCount;
    const container = document.getElementById('fabric-rows');
    const div = document.createElement('div');
    div.className = 'item-row-group';
    div.id = `row-${idx}`;
    div.innerHTML = `
      <div class="row-header">
        <span class="row-title">Entry #${idx}</span>
        ${idx > 1 ? `<button type="button" class="remove-row-btn" onclick="removeRow(${idx})" title="Remove">✕</button>` : ''}
      </div>
      <div class="form-group">
        <label class="form-label">Colour</label>
        <input type="text" class="form-control" name="colour_${idx}" placeholder="Enter colour name" required value="${esc(data.colour || '')}">
      </div>
      <div class="form-group">
        <label class="form-label">GSM</label>
        <input type="number" class="form-control" name="gsm_${idx}" placeholder="Enter GSM value" min="1" required value="${esc(data.gsm || '')}">
      </div>
      <div class="form-group">
        <label class="form-label">Weight</label>
        <div class="input-unit-wrap">
          <input type="number" class="form-control" name="weight_${idx}" placeholder="Enter weight" step="0.01" min="0.01" required value="${esc(data.weight || '')}" oninput="calcRowTotal(${idx})">
          <span class="unit-label">KG</span>
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">Rib</label>
        <div class="input-unit-wrap">
          <input type="number" class="form-control" name="rib_${idx}" placeholder="Enter rib" step="0.01" min="0" value="${esc(data.rib ?? 0)}" value="0">
          <span class="unit-label">g</span>
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">Price per KG</label>
        <div class="input-unit-wrap">
          <span class="unit-label" style="border-radius:var(--radius-sm) 0 0 var(--radius-sm); border-right:none; border-left:1px solid var(--border);">₹</span>
          <input type="number" class="form-control" name="pricePerKg_${idx}" placeholder="Enter price" step="0.01" min="0.01" required value="${esc(data.pricePerKg || '')}" oninput="calcRowTotal(${idx})" style="border-radius:0 var(--radius-sm) var(--radius-sm) 0;">
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">Row Total Price</label>
        <div class="calc-display">
          <span class="currency-symbol">₹</span>
          <input type="text" class="form-control" id="rowTotal_${idx}" readonly placeholder="Auto-calculated" value="${esc(data.rowTotal || '')}">
        </div>
      </div>`;
    container.appendChild(div);
    if (data.weight && data.pricePerKg) calcRowTotal(idx);
}

function removeRow(idx) {
    const el = document.getElementById(`row-${idx}`);
    if (el) { el.remove(); calcTotalPrice(); }
}

function calcRowTotal(idx) {
    const w = parseFloat(document.querySelector(`[name="weight_${idx}"]`)?.value) || 0;
    const p = parseFloat(document.querySelector(`[name="pricePerKg_${idx}"]`)?.value) || 0;
    const rt = w * p;
    const el = document.getElementById(`rowTotal_${idx}`);
    if (el) el.value = rt.toFixed(2);
    calcTotalPrice();
}

function calcTotalPrice() {
    let total = 0;
    document.querySelectorAll('[id^="rowTotal_"]').forEach(el => {
        total += parseFloat(el.value) || 0;
    });
    document.getElementById('total-price-display').textContent = formatCurrency(total);
    document.getElementById('total-price-hidden').value = total.toFixed(2);
}

async function loadExistingFabric(id) {
    try {
        const res = await apiCall('GET', `/api/purchases/fabrics/${id}`);
        const f   = res.data;

        // Populate Fabric Name dropdown
        setDropdownOrManual('fabric-name-select', 'fabric-name-manual-wrap', 'fabric-name-manual', f.fabricName);
        // Populate Fabric Type dropdown
        setDropdownOrManual('fabric-type-select', 'fabric-type-manual-wrap', 'fabric-type-manual', f.fabricType);

        // Remove any default rows then recreate from data
        document.getElementById('fabric-rows').innerHTML = '';
        fabricRowCount = 0;
        (f.fabricItems || []).forEach(item => addFabricRow(item));
        if (!f.fabricItems || f.fabricItems.length === 0) addFabricRow();

    } catch (ex) {
        showToast('Failed to load fabric data: ' + ex.message, 'error');
    }
}

function setDropdownOrManual(selectId, wrapId, inputId, value) {
    const sel  = document.getElementById(selectId);
    const wrap = document.getElementById(wrapId);
    const inp  = document.getElementById(inputId);
    // Check if value matches an option
    const exists = Array.from(sel.options).some(o => o.value === value);
    if (exists) {
        sel.value = value;
        wrap.classList.remove('show');
        inp.required = false;
    } else {
        sel.value = 'Others';
        wrap.classList.add('show');
        inp.required = true;
        inp.value = value;
    }
}

async function handleSubmit(e) {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');
    btn.disabled = true;
    btn.textContent = editId ? 'Updating…' : 'Submitting…';

    try {
        const payload = buildPayload();
        if (editId) {
            await apiCall('PUT', `/api/purchases/fabrics/${editId}`, payload);
            showToast('Fabric purchase updated successfully!', 'success');
            setTimeout(() => { window.location.href = '/pages/fabric-history.html'; }, 1000);
        } else {
            await apiCall('POST', '/api/purchases/fabrics', payload);
            showToast('Fabric purchase saved!', 'success');
            setTimeout(() => { window.location.href = '/pages/dashboard.html'; }, 1000);
        }
    } catch (ex) {
        showToast(ex.message, 'error');
        btn.disabled = false;
        btn.textContent = editId ? 'Update' : 'Submit';
    }
}

function buildPayload() {
    // Fabric Name
    const nameSelect = document.getElementById('fabric-name-select').value;
    const fabricName = nameSelect === 'Others'
        ? document.getElementById('fabric-name-manual').value.trim()
        : nameSelect;

    // Fabric Type
    const typeSelect = document.getElementById('fabric-type-select').value;
    const fabricType = typeSelect === 'Others'
        ? document.getElementById('fabric-type-manual').value.trim()
        : typeSelect;

    // Items
    const rows = document.querySelectorAll('.item-row-group');
    const fabricItems = Array.from(rows).map(row => {
        const idx = row.id.replace('row-', '');
        return {
            colour:     row.querySelector(`[name="colour_${idx}"]`).value.trim(),
            gsm:        parseInt(row.querySelector(`[name="gsm_${idx}"]`).value, 10),
            weight:     parseFloat(row.querySelector(`[name="weight_${idx}"]`).value),
            rib:        parseFloat(row.querySelector(`[name="rib_${idx}"]`).value) || 0,
            pricePerKg: parseFloat(row.querySelector(`[name="pricePerKg_${idx}"]`).value)
        };
    });

    return { fabricName, fabricType, fabricItems };
}

function esc(v) {
    return String(v ?? '').replace(/"/g, '&quot;');
}
