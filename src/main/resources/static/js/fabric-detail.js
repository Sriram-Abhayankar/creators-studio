// fabric-detail.js — Shows a single fabric purchase with edit & delete
document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    const id = getQueryParam('id');
    if (!id) { window.location.href = '/pages/fabric-history.html'; return; }

    document.getElementById('go-back-btn').addEventListener('click', () => {
        window.location.href = '/pages/fabric-history.html';
    });
    document.getElementById('edit-btn').addEventListener('click', () => {
        window.location.href = `/pages/fabric-purchase.html?id=${id}`;
    });
    document.getElementById('delete-btn').addEventListener('click', () => handleDelete(id));

    const content = document.getElementById('detail-content');
    content.innerHTML = '<div style="text-align:center;padding:2rem"><div class="spinner"></div></div>';

    try {
        const res = await apiCall('GET', `/api/purchases/fabrics/${id}`);
        const f   = res.data;

        // Build items table rows
        const itemRows = (f.fabricItems || []).map(item => `
          <tr>
            <td>${esc(item.colour)}</td>
            <td>${item.gsm}</td>
            <td>${item.weight} KG</td>
            <td>${item.rib} g</td>
            <td>${formatCurrency(item.pricePerKg)}</td>
            <td>${formatCurrency(item.rowTotal)}</td>
          </tr>`).join('');

        content.innerHTML = `
          <div class="detail-header">
            <div class="record-id">Record #${f.id}</div>
            <h2>${esc(f.fabricName)}</h2>
            <div class="detail-meta">${esc(f.fabricType)} &nbsp;·&nbsp; ${formatDate(f.purchaseDate)}</div>
          </div>

          <p class="section-title">Colour Entries</p>
          <div style="overflow-x:auto">
            <table class="items-table">
              <thead>
                <tr>
                  <th>Colour</th><th>GSM</th><th>Weight</th><th>Rib</th><th>Price/KG</th><th>Row Total</th>
                </tr>
              </thead>
              <tbody>${itemRows}</tbody>
            </table>
          </div>

          <div class="total-price-bar">
            <span class="tpb-label">Total Price</span>
            <span class="tpb-value">${formatCurrency(f.totalPrice)}</span>
          </div>`;
    } catch (ex) {
        content.innerHTML = `<div class="empty-state"><p style="color:var(--error)">${esc(ex.message)}</p></div>`;
    }
});

async function handleDelete(id) {
    if (!confirmAction('Are you sure you want to delete this fabric purchase? This cannot be undone.')) return;
    try {
        await apiCall('DELETE', `/api/purchases/fabrics/${id}`);
        showToast('Fabric purchase deleted.', 'success');
        setTimeout(() => { window.location.href = '/pages/fabric-history.html'; }, 900);
    } catch (ex) {
        showToast('Delete failed: ' + ex.message, 'error');
    }
}

function esc(v) { return String(v ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
