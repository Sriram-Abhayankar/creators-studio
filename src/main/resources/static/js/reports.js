// reports.js — Logic for Creators Studio reports page
document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    document.getElementById('go-back-btn').addEventListener('click', () => {
        window.location.href = '/pages/dashboard.html';
    });

    // Wire exports button
    document.getElementById('export-pdf-btn').addEventListener('click', () => handleExport('/api/reports/export/pdf'));
    document.getElementById('export-excel-btn').addEventListener('click', () => handleExport('/api/reports/export/excel'));

    await loadReportsData();
});

async function loadReportsData() {
    let grandTotal = 0;

    // 1. Load Summary Info
    try {
        const res = await apiCall('GET', '/api/reports/summary');
        const s = res.data;
        grandTotal = parseFloat(s.totalExpense || 0);

        document.getElementById('fabric-exp').textContent = formatCurrency(s.fabricExpense);
        document.getElementById('accessory-exp').textContent = formatCurrency(s.accessoryExpense);
        document.getElementById('total-exp').textContent = formatCurrency(s.totalExpense);
    } catch (ex) {
        showToast('Failed to load expense summary: ' + ex.message, 'error');
        return;
    }

    // 2. Load Category Breakdown Info
    try {
        const res = await apiCall('GET', '/api/reports/category');
        const list = res.data || [];
        const tbody = document.getElementById('category-rows');
        tbody.innerHTML = '';

        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" style="text-align:center; color:var(--text-light);">No category data found.</td></tr>';
        } else {
            list.forEach(c => {
                const amount = parseFloat(c.totalAmount || 0);
                const percent = grandTotal > 0 ? (amount / grandTotal) * 100 : 0;
                
                const tr = document.createElement('tr');
                tr.innerHTML = `
                  <td style="font-weight:600;">${esc(c.categoryName)}</td>
                  <td style="font-weight:600; color:var(--primary);">${formatCurrency(amount)}</td>
                  <td>
                    <div class="progress-bar-container">
                      <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: ${percent.toFixed(1)}%;"></div>
                      </div>
                      <span class="progress-percent">${percent.toFixed(1)}%</span>
                    </div>
                  </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (ex) {
        document.getElementById('category-rows').innerHTML = `<tr><td colspan="3" style="text-align:center; color:var(--error);">${esc(ex.message)}</td></tr>`;
    }

    // 3. Load Monthly Timeline Info
    try {
        const res = await apiCall('GET', '/api/reports/monthly');
        const list = res.data || [];
        const tbody = document.getElementById('monthly-rows');
        tbody.innerHTML = '';

        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color:var(--text-light);">No monthly data found.</td></tr>';
        } else {
            const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            list.forEach(m => {
                const monthName = months[m.month - 1] || m.month;
                const tr = document.createElement('tr');
                tr.innerHTML = `
                  <td style="font-weight:600;">${m.year} ${monthName}</td>
                  <td>${formatCurrency(m.fabricExpense)}</td>
                  <td>${formatCurrency(m.accessoryExpense)}</td>
                  <td style="font-weight:700; color:var(--primary);">${formatCurrency(m.totalExpense)}</td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (ex) {
        document.getElementById('monthly-rows').innerHTML = `<tr><td colspan="4" style="text-align:center; color:var(--error);">${esc(ex.message)}</td></tr>`;
    }
}

async function handleExport(endpoint) {
    try {
        await apiCall('GET', endpoint);
        showToast('Export successful', 'success');
    } catch (ex) {
        // The stubs throw UnsupportedOperationException caught by the exception handler returning HTTP 501
        showToast(ex.message, 'warning');
    }
}

function esc(v) { return String(v ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
