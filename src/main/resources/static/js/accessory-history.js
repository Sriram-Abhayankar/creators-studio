// accessory-history.js — Lists all accessory purchases with filtering and sorting
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
    loadAccessoryHistory();

    // Enable/disable date filters based on selected type
    function updateDateInputState() {
        const type = typeSelect.value;
        if (type === 'SIZE_PATTERN' || type === 'OTHERS') {
            startDateInput.value = '';
            endDateInput.value = '';
            startDateInput.disabled = true;
            endDateInput.disabled = true;
            startDateInput.style.opacity = '0.5';
            endDateInput.style.opacity = '0.5';
        } else {
            startDateInput.disabled = false;
            endDateInput.disabled = false;
            startDateInput.style.opacity = '1';
            endDateInput.style.opacity = '1';
        }
    }

    typeSelect.addEventListener('change', () => {
        updateDateInputState();
        loadAccessoryHistory();
    });

    // Event listeners for automatic re-filtering
    const filterInputs = [nameInput, startDateInput, endDateInput, sortSelect];
    filterInputs.forEach(el => {
        el.addEventListener('input', () => {
            loadAccessoryHistory();
        });
    });

    clearBtn.addEventListener('click', () => {
        nameInput.value = '';
        typeSelect.value = '';
        startDateInput.value = '';
        endDateInput.value = '';
        sortSelect.value = 'latest';
        updateDateInputState();
        loadAccessoryHistory();
    });
});

async function loadAccessoryHistory() {
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
        if (name) params.append('accessoryName', name);
        if (type) params.append('type', type);
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);
        if (sortBy) params.append('sortBy', sortBy);

        const url = `/api/purchases/accessories?${params.toString()}`;
        const res = await apiCall('GET', url);
        const items = res.data || [];

        if (items.length === 0) {
            const hasFilters = name || type || startDate || endDate;
            if (hasFilters) {
                list.innerHTML = `
                  <div class="empty-state">
                    <div class="empty-icon">🧶</div>
                    <p>No accessory purchases match the criteria.</p>
                  </div>`;
            } else {
                list.innerHTML = `
                  <div class="empty-state">
                    <div class="empty-icon">🧶</div>
                    <p>No accessory purchases recorded yet.</p>
                    <a href="/pages/accessory-purchase.html" class="btn btn-outline btn-sm">Add First Purchase</a>
                  </div>`;
            }
            return;
        }

        list.innerHTML = '';
        items.forEach(a => {
            const typeCls = { CONE: 'cone', SIZE_PATTERN: 'size-pattern', OTHERS: 'others' }[a.type] || '';
            const item = document.createElement('div');
            item.className = 'history-item';
            item.innerHTML = `
              <div class="item-main">
                <div class="item-name">${esc(a.accessoryName)}</div>
                <div class="item-meta">
                  <span class="type-badge ${typeCls}">${formatType(a.type)}</span>
                </div>
              </div>
              <span class="item-price">${formatCurrency(a.totalPrice)}</span>
              <span class="chevron">›</span>`;
            item.addEventListener('click', () => {
                window.location.href = `/pages/accessory-detail.html?id=${a.id}`;
            });
            list.appendChild(item);
        });
    } catch (ex) {
        list.innerHTML = `<div class="empty-state"><p style="color:var(--error)">${esc(ex.message)}</p></div>`;
    }
}

function esc(v) { return String(v ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
