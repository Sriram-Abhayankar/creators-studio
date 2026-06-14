// dashboard.js — Dashboard page logic
document.addEventListener('DOMContentLoaded', async () => {
    requireAuth();

    const session = getSession();
    const greet   = document.getElementById('user-greeting');
    if (greet) greet.textContent = `Welcome, ${session.username}`;

    document.getElementById('logout-btn').addEventListener('click', () => { logout(); });
    document.getElementById('purchasing-btn').addEventListener('click', () => {
        window.location.href = '/pages/purchasing-bills.html';
    });
    document.getElementById('reports-btn').addEventListener('click', () => {
        window.location.href = '/pages/reports.html';
    });

    const fabricList = document.getElementById('recent-fabrics-list');
    const accList = document.getElementById('recent-accessories-list');

    // Load dashboard summary
    try {
        const res = await apiCall('GET', '/api/dashboard');
        const d   = res.data;
        document.getElementById('fabric-count').textContent  = d.totalFabricPurchases;
        document.getElementById('acc-count').textContent     = d.totalAccessoryPurchases;
        document.getElementById('fabric-exp').textContent    = formatCurrency(d.fabricExpense);
        document.getElementById('acc-exp').textContent       = formatCurrency(d.accessoryExpense);
        document.getElementById('total-exp').textContent     = formatCurrency(d.totalExpense);

        // Render Recent Fabrics
        if (d.recentFabrics && d.recentFabrics.length > 0) {
            fabricList.innerHTML = '';
            d.recentFabrics.forEach(f => {
                const item = document.createElement('div');
                item.style.cssText = 'display:flex; justify-content:space-between; align-items:center; font-size:0.82rem; padding:0.45rem; border-radius:var(--radius-sm); border:1px solid var(--border); background:var(--bg-card); cursor:pointer; transition:background 0.2s;';
                item.innerHTML = `
                  <div style="min-width:0; overflow:hidden;">
                    <div style="font-weight:600; color:var(--text-primary); white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${esc(f.fabricName)}</div>
                    <div style="font-size:0.72rem; color:var(--text-secondary); white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${esc(f.fabricType)} · ${formatDate(f.purchaseDate)}</div>
                  </div>
                  <div style="font-weight:700; color:var(--text-primary); margin-left:0.5rem; flex-shrink:0;">${formatCurrency(f.totalPrice)}</div>
                `;
                item.addEventListener('click', () => {
                    window.location.href = `/pages/fabric-detail.html?id=${f.id}`;
                });
                // subtle hover effect
                item.addEventListener('mouseenter', () => item.style.background = 'var(--bg-hover)');
                item.addEventListener('mouseleave', () => item.style.background = 'var(--bg-card)');
                fabricList.appendChild(item);
            });
        } else {
            fabricList.innerHTML = '<div style="font-size:0.8rem; color:var(--text-muted); text-align:center; padding:1rem;">No recent fabrics.</div>';
        }

        // Render Recent Accessories
        if (d.recentAccessories && d.recentAccessories.length > 0) {
            accList.innerHTML = '';
            d.recentAccessories.forEach(a => {
                const item = document.createElement('div');
                item.style.cssText = 'display:flex; justify-content:space-between; align-items:center; font-size:0.82rem; padding:0.45rem; border-radius:var(--radius-sm); border:1px solid var(--border); background:var(--bg-card); cursor:pointer; transition:background 0.2s;';
                item.innerHTML = `
                  <div style="min-width:0; overflow:hidden;">
                    <div style="font-weight:600; color:var(--text-primary); white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${esc(a.accessoryName)}</div>
                    <div style="font-size:0.72rem; color:var(--text-secondary); white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${formatType(a.type)}${a.purchaseDate ? ' · ' + formatDate(a.purchaseDate) : ''}</div>
                  </div>
                  <div style="font-weight:700; color:var(--text-primary); margin-left:0.5rem; flex-shrink:0;">${formatCurrency(a.totalPrice)}</div>
                `;
                item.addEventListener('click', () => {
                    window.location.href = `/pages/accessory-detail.html?id=${a.id}`;
                });
                item.addEventListener('mouseenter', () => item.style.background = 'var(--bg-hover)');
                item.addEventListener('mouseleave', () => item.style.background = 'var(--bg-card)');
                accList.appendChild(item);
            });
        } else {
            accList.innerHTML = '<div style="font-size:0.8rem; color:var(--text-muted); text-align:center; padding:1rem;">No recent accessories.</div>';
        }

    } catch (ex) {
        console.warn('Dashboard summary load failed:', ex.message);
        fabricList.innerHTML = '<div style="font-size:0.8rem; color:var(--error); text-align:center; padding:1rem;">Load failed.</div>';
        accList.innerHTML = '<div style="font-size:0.8rem; color:var(--error); text-align:center; padding:1rem;">Load failed.</div>';
    }
});

function esc(v) { return String(v ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
