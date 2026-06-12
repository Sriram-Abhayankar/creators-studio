// fabric-history.js — Lists all fabric purchases with filtering and sorting
document.addEventListener('DOMContentLoaded', () => {
    requireAuth();

    document.getElementById('go-back-btn').addEventListener('click', () => {
        window.location.href = '/pages/dashboard.html';
    });

    const nameInput = document.getElementById('filter-name');
    const typeSelect = document.getElementById('filter-type');
    const startDateInput = document.getElementById('filter-start-date');
    const endDateInput = document.getElementById('filter-end-date');
    const sortSelect = document.getElementById('filter-sort');
    const clearBtn = document.getElementById('clear-filters-btn');

    // Load data initially
    loadFabricHistory();

    // Event listeners for automatic re-filtering
    const filterInputs = [nameInput, typeSelect, startDateInput, endDateInput, sortSelect];
    filterInputs.forEach(el => {
        el.addEventListener('input', () => {
            loadFabricHistory();
        });
    });

    clearBtn.addEventListener('click', () => {
        nameInput.value = '';
        typeSelect.value = '';
        startDateInput.value = '';
        endDateInput.value = '';
        sortSelect.value = 'latest';
        loadFabricHistory();
    });
});

async function loadFabricHistory() {
    const list = document.getElementById('history-list');
    list.innerHTML = '<div class="empty-state"><div class="spinner"></div></div>';

    const name = document.getElementById('filter-name').value;
    const type = document.getElementById('filter-type').value;
    const startDate = document.getElementById('filter-start-date').value;
    const endDate = document.getElementById('filter-end-date').value;
    const sortBy = document.getElementById('filter-sort').value;

    // Frontend validation: startDate <= endDate
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
        list.innerHTML = '<div class="empty-state"><p style="color:var(--error)">Start date cannot be after end date</p></div>';
        showToast('Start date cannot be after end date', 'error');
        return;
    }

    try {
        const params = new URLSearchParams();
        if (name) params.append('fabricName', name);
        if (type) params.append('fabricType', type);
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);
        if (sortBy) params.append('sortBy', sortBy);

        const url = `/api/purchases/fabrics?${params.toString()}`;
        const res = await apiCall('GET', url);
        const items = res.data || [];

        if (items.length === 0) {
            const hasFilters = name || type || startDate || endDate;
            if (hasFilters) {
                list.innerHTML = `
                  <div class="empty-state">
                    <div class="empty-icon">🧵</div>
                    <p>No fabric purchases match the criteria.</p>
                  </div>`;
            } else {
                list.innerHTML = `
                  <div class="empty-state">
                    <div class="empty-icon">🧵</div>
                    <p>No fabric purchases recorded yet.</p>
                    <a href="/pages/fabric-purchase.html" class="btn btn-outline btn-sm">Add First Purchase</a>
                  </div>`;
            }
            return;
        }

        list.innerHTML = '';
        items.forEach(f => {
            const item = document.createElement('div');
            item.className = 'history-item';
            item.style.cursor = 'pointer';
            item.innerHTML = `
              <div class="item-main">
                <div class="item-name">${esc(f.fabricName)}</div>
                <div class="item-meta">${esc(f.fabricType)} &nbsp;·&nbsp; ${formatDate(f.purchaseDate)}</div>
              </div>
              <span class="item-price">${formatCurrency(f.totalPrice)}</span>
              <span class="chevron">›</span>`;
            item.addEventListener('click', () => {
                window.location.href = `/pages/fabric-detail.html?id=${f.id}`;
            });
            list.appendChild(item);
        });
    } catch (ex) {
        list.innerHTML = `<div class="empty-state"><p style="color:var(--error)">${esc(ex.message)}</p></div>`;
    }
}

function esc(v) { return String(v ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
