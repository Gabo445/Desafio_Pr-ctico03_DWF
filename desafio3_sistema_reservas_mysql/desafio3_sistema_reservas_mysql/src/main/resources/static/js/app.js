const API_URL = window.location.origin;
const TOKEN_KEY = 'desafio3_jwt_token';

const $ = (id) => document.getElementById(id);

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

function showMessage(text, type = 'info') {
    const box = $('message');
    box.textContent = text;
    box.className = `message ${type}`;
    box.classList.remove('hidden');
    setTimeout(() => box.classList.add('hidden'), 6000);
}

function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
    };
}

async function request(path, options = {}) {
    const response = await fetch(`${API_URL}${path}`, options);
    const text = await response.text();
    let data = null;
    try { data = text ? JSON.parse(text) : null; } catch { data = text; }

    if (!response.ok) {
        const msg = data?.message || data?.error || 'Error en la petición';
        if (response.status === 401 || response.status === 403) {
            clearToken();
            updateUI();
            throw new Error('Token inválido o expirado. Inicia sesión nuevamente.');
        }
        throw new Error(msg);
    }
    return data;
}

function updateUI() {
    const logged = !!getToken();
    $('authSection').classList.toggle('hidden', logged);
    $('appSection').classList.toggle('hidden', !logged);
    $('btnLogout').classList.toggle('hidden', !logged);
    if (logged) {
        loadEvents();
        loadBookings();
    }
}

async function login() {
    try {
        const body = {
            username: $('loginUsername').value.trim(),
            password: $('loginPassword').value
        };
        const data = await request('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        setToken(data.token);
        showMessage('Inicio de sesión correcto.', 'success');
        updateUI();
    } catch (err) {
        showMessage(err.message || 'Credenciales incorrectas.', 'error');
    }
}

async function register() {
    try {
        const body = {
            username: $('registerUsername').value.trim(),
            password: $('registerPassword').value,
            firstname: $('registerFirstname').value.trim(),
            lastname: $('registerLastname').value.trim(),
            age: Number($('registerAge').value)
        };
        await request('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        showMessage('Usuario registrado. Ahora puedes iniciar sesión.', 'success');
    } catch (err) {
        showMessage(err.message, 'error');
    }
}

async function loadEvents() {
    try {
        const events = await request('/api/events', { headers: authHeaders() });
        const list = $('eventsList');
        list.innerHTML = '';
        if (!events || events.length === 0) {
            list.innerHTML = '<p>No hay eventos disponibles.</p>';
            return;
        }
        events.forEach(event => {
            const card = document.createElement('article');
            card.className = 'card';
            card.innerHTML = `
                <span class="badge">ID: ${event.idEvent}</span>
                <h3>${event.title}</h3>
                <p><b>Lugar:</b> ${event.venue}</p>
                <p><b>Fecha:</b> ${formatDate(event.eventDate)}</p>
                <p><b>Precio:</b> $${Number(event.pricePerTicket).toFixed(2)}</p>
                <button class="btn secondary" onclick="showEventDetail(${event.idEvent}, this)">Ver detalle</button>
                <div class="detail hidden"></div>
                <label>Cantidad de entradas</label>
                <input type="number" min="1" value="1" class="quantityInput">
                <button class="btn primary" onclick="createBooking(${event.idEvent}, this)">Reservar</button>
            `;
            list.appendChild(card);
        });
    } catch (err) {
        showMessage(err.message, 'error');
    }
}

async function showEventDetail(id, button) {
    try {
        const event = await request(`/api/events/${id}`, { headers: authHeaders() });
        const detail = button.parentElement.querySelector('.detail');
        detail.innerHTML = `
            <p><b>Descripción:</b> ${event.description || 'Sin descripción'}</p>
            <p><b>Capacidad:</b> ${event.capacity} personas</p>
            <p><b>Fecha completa:</b> ${formatDate(event.eventDate)}</p>
        `;
        detail.classList.toggle('hidden');
    } catch (err) {
        showMessage(err.message, 'error');
    }
}

async function createBooking(eventId, button) {
    try {
        const quantity = Number(button.parentElement.querySelector('.quantityInput').value);
        await request('/api/bookings', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({ eventId, quantity })
        });
        showMessage('Reserva creada correctamente.', 'success');
        loadBookings();
    } catch (err) {
        showMessage(err.message, 'error');
    }
}

async function loadBookings() {
    try {
        const bookings = await request('/api/bookings/my', { headers: authHeaders() });
        const list = $('bookingsList');
        list.innerHTML = '';
        if (!bookings || bookings.length === 0) {
            list.innerHTML = '<p>No tienes reservas registradas.</p>';
            return;
        }
        bookings.forEach(booking => {
            const card = document.createElement('article');
            card.className = 'card';
            const cancelled = booking.status === 'CANCELLED';
            card.innerHTML = `
                <span class="badge">${booking.status}</span>
                <h3>${booking.event?.title || 'Evento'}</h3>
                <p><b>Cantidad:</b> ${booking.quantity}</p>
                <p><b>Total:</b> $${Number(booking.totalAmount).toFixed(2)}</p>
                <p><b>Fecha reserva:</b> ${formatDate(booking.bookingDate)}</p>
                <p><b>Lugar:</b> ${booking.event?.venue || ''}</p>
                ${cancelled ? '' : `<button class="btn danger" onclick="cancelBooking(${booking.idBooking})">Cancelar reserva</button>`}
            `;
            list.appendChild(card);
        });
    } catch (err) {
        showMessage(err.message, 'error');
    }
}

async function cancelBooking(id) {
    try {
        await request(`/api/bookings/${id}`, {
            method: 'DELETE',
            headers: authHeaders()
        });
        showMessage('Reserva cancelada correctamente.', 'success');
        loadBookings();
    } catch (err) {
        showMessage(err.message, 'error');
    }
}

async function createEvent() {
    try {
        const body = {
            title: $('eventTitle').value.trim(),
            description: $('eventDescription').value.trim(),
            eventDate: $('eventDate').value,
            venue: $('eventVenue').value.trim(),
            capacity: Number($('eventCapacity').value),
            pricePerTicket: Number($('eventPrice').value)
        };
        await request('/api/events', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify(body)
        });
        showMessage('Evento creado correctamente.', 'success');
        loadEvents();
        showView('eventsView');
    } catch (err) {
        showMessage(err.message, 'error');
    }
}

function showView(viewId) {
    document.querySelectorAll('.view').forEach(v => v.classList.add('hidden'));
    $(viewId).classList.remove('hidden');
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelector(`[data-view="${viewId}"]`).classList.add('active');
    if (viewId === 'eventsView') loadEvents();
    if (viewId === 'bookingsView') loadBookings();
}

function logout() {
    clearToken();
    updateUI();
    showMessage('Sesión cerrada. El token fue eliminado.', 'info');
}

function formatDate(value) {
    if (!value) return 'Sin fecha';
    return new Date(value).toLocaleString('es-SV');
}

$('btnLogin').addEventListener('click', login);
$('btnRegister').addEventListener('click', register);
$('btnLogout').addEventListener('click', logout);
$('btnLoadEvents').addEventListener('click', loadEvents);
$('btnLoadBookings').addEventListener('click', loadBookings);
$('btnCreateEvent').addEventListener('click', createEvent);
document.querySelectorAll('.tab').forEach(tab => tab.addEventListener('click', () => showView(tab.dataset.view)));

updateUI();
