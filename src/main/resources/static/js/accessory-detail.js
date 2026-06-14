// accessory-detail.js — Shows a single accessory purchase with edit & delete
document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    const id = getQueryParam('id');
    if (!id) { window.location.href = '/pages/accessory-history.html'; return; }

    document.getElementById('go-back-btn').addEventListener('click', () => {
        window.location.href = '/pages/accessory-history.html';
    });
    document.getElementById('delete-btn').addEventListener('click', () => handleDelete(id));

    const content = document.getElementById('detail-content');
    content.innerHTML = '<div style="text-align:center;padding:2rem"><div class="spinner"></div></div>';

    try {
        const res = await apiCall('GET', `/api/purchases/accessories/${id}`);
        const a   = res.data;
        const typeCls = { CONE: 'cone', SIZE_PATTERN: 'size-pattern', OTHERS: 'others' }[a.type] || '';

        // Wire edit button once we know the type
        document.getElementById('edit-btn').addEventListener('click', () => {
            window.location.href = `/pages/accessory-purchase.html?id=${id}&type=${a.type}`;
        });

        let detailSection = '';
        if (a.type === 'CONE' && a.cone) {
            const rows = (a.cone.coneItems || []).map(i => `
              <tr>
                <td>${esc(i.colourName)}</td>
                <td>${esc(i.colourCode)}</td>
                <td>${i.unit}</td>
                <td>${formatCurrency(i.pricePerUnit)}</td>
                <td>${formatCurrency(i.rowTotal)}</td>
              </tr>`).join('');
            detailSection = `
              <p class="section-title">Cone Items</p>
              <div style="overflow-x:auto;margin-top:0.5rem">
                <table class="items-table">
                  <thead><tr><th>Colour Name</th><th>Code</th><th>Unit</th><th>Price/Unit</th><th>Row Total</th></tr></thead>
                  <tbody>${rows}</tbody>
                </table>
              </div>`;
        } else if (a.type === 'SIZE_PATTERN' && a.sizePattern) {
            const sp = a.sizePattern;
            detailSection = `
              <p class="section-title">Size Pattern Details</p>
              <div class="detail-field"><span class="field-label">Brand Name</span><span class="field-value">${esc(sp.brandName)}</span></div>
              <div class="detail-field"><span class="field-label">Style Number</span><span class="field-value">${esc(sp.styleNumber)}</span></div>
              <div class="detail-field"><span class="field-label">Style Name</span><span class="field-value">${esc(sp.styleName)}</span></div>
              <div class="detail-field"><span class="field-label">Price</span><span class="field-value">${formatCurrency(sp.price)}</span></div>`;
        } else if (a.type === 'OTHERS' && a.others) {
            const o = a.others;
            detailSection = `
              <p class="section-title">Others Details</p>
              <div class="detail-field"><span class="field-label">Item Name</span><span class="field-value">${esc(o.itemsName)}</span></div>
              <div class="detail-field"><span class="field-label">Unit / Kg / Gross</span><span class="field-value">${esc(o.unit)}</span></div>
              <div class="detail-field"><span class="field-label">Price</span><span class="field-value">${formatCurrency(o.price)}</span></div>`;
        }

        content.innerHTML = `
          <div class="detail-header">
            <div class="record-id">Record #${a.id}</div>
            <h2>${esc(a.accessoryName)}</h2>
            <div class="detail-meta">
              <span class="type-badge ${typeCls}">${formatType(a.type)}</span>
              <span style="color:var(--text-light); font-size:0.85rem; margin-left:0.5rem;">${a.purchaseDate ? formatDate(a.purchaseDate) : ''}</span>
            </div>
          </div>
          ${detailSection}
          <div class="total-price-bar" style="margin-top:1rem">
            <span class="tpb-label">Total Price</span>
            <span class="tpb-value">${formatCurrency(a.totalPrice)}</span>
          </div>`;
    } catch (ex) {
        content.innerHTML = `<div class="empty-state"><p style="color:var(--error)">${esc(ex.message)}</p></div>`;
    }
});

async function handleDelete(id) {
    if (!confirmAction('Are you sure you want to delete this accessory purchase? This cannot be undone.')) return;
    try {
        await apiCall('DELETE', `/api/purchases/accessories/${id}`);
        showToast('Accessory purchase deleted.', 'success');
        setTimeout(() => { window.location.href = '/pages/accessory-history.html'; }, 900);
    } catch (ex) {
        showToast('Delete failed: ' + ex.message, 'error');
    }
}

function esc(v) { return String(v ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
