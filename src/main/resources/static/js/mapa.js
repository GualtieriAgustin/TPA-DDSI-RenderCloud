// Variables globales para el mapa y el panel
let map;
const markers = {};
let lastSelectedMarkerId = null;
let panel;
let panelToggleButton;

document.addEventListener('DOMContentLoaded', function () {
    const hechosJson = document.getElementById('hechos-json-data').textContent;
    const hechosArray = JSON.parse(hechosJson);

    const hechosData = hechosArray.reduce((obj, hecho) => {
        obj[hecho.id] = hecho;
        return obj;
    }, {});

    initMap(hechosData);
});

/**
 * Ajusta la posición y altura del panel lateral según la altura de la navbar.
 */
function adjustPanelPosition() {
    const navbar = document.querySelector('.navbar.sticky-top');
    if (navbar && panel) {
        const navbarHeight = navbar.offsetHeight;
        const windowHeight = window.innerHeight;
        panel.style.top = `${navbarHeight}px`;
        panel.style.height = `${windowHeight - navbarHeight}px`;
    }
}

/**
 * Muestra el panel lateral.
 */
const showPanel = () => {
    panel.classList.add('visible');
    panelToggleButton.style.display = 'flex';
    if (map) map.invalidateSize();
};

/**
 * Oculta el panel lateral.
 */
const hidePanel = () => {
    panel.classList.remove('visible');
    if (map) map.invalidateSize();
};

/**
 * Centra el mapa en un marcador, desplazándolo si el panel lateral está visible.
 * @param {L.Marker} marker - El marcador de Leaflet.
 */
function centerMapOnMarker(marker) {
    const zoomLevel = 14;
    const panelWidth = panel.offsetWidth;
    const offset = [-panelWidth, 0];

    map.setView(marker.getLatLng(), zoomLevel, {animate: false});
    if (panel.classList.contains('visible')) {
        map.panBy(offset, {animate: false});
    }
}

/**
 * Popula el carrusel de multimedia en el panel lateral.
 * @param {Array} multimedias - Un array de objetos multimedia.
 */
function populateCarousel(multimedias) {
    const multimediaCarousel = document.getElementById('hecho-multimedia-carousel');
    const carouselInner = document.getElementById('multimedia-carousel-inner');
    carouselInner.innerHTML = '';

    if (multimedias && multimedias.length > 0) {
        multimediaCarousel.style.display = 'block';
        multimedias.forEach((media, index) => {
            let carouselItem = document.createElement('div');
            carouselItem.className = `carousel-item ${index === 0 ? 'active' : ''}`;

            let mediaElement;
            if (media.tipo === 'IMAGEN') {
                mediaElement = document.createElement('img');
                mediaElement.src = media.url;
                mediaElement.alt = media.descripcion || 'Imagen del hecho';
                mediaElement.className = 'd-block w-100';
            } else if (media.tipo === 'VIDEO') {
                carouselItem.classList.add('has-video');
                mediaElement = document.createElement('video');
                mediaElement.src = media.url;
                mediaElement.controls = true;
                mediaElement.className = 'd-block w-100';
            }

            if (mediaElement) {
                carouselItem.appendChild(mediaElement);

                // Si hay una descripción, la añadimos en un caption
                if (media.descripcion) {
                    const caption = document.createElement('div');
                    caption.className = 'carousel-caption';
                    caption.innerHTML = `<h5>${media.descripcion}</h5>`;
                    carouselItem.appendChild(caption);
                }
                carouselInner.appendChild(carouselItem);
            }
        });
        new bootstrap.Carousel(multimediaCarousel);
    } else {
        multimediaCarousel.style.display = 'none';
    }
}

function formatCustomDateTime(date) {
    const day = date.getDate();
    const month = date.getMonth() + 1; // Los meses son base 0
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${day}/${month}/${year} ${hours}:${minutes}`;
}


/**
 * Inicializa los marcadores en el mapa a partir de los datos de los hechos.
 * @param {Object} hechosData - Objeto con los datos de los hechos.
 */
function initMarkers(hechosData) {
    Object.values(hechosData).forEach(hecho => {
        if (hecho.ubicacion && hecho.ubicacion.latitud) {
            markers[hecho.id] = L.marker([hecho.ubicacion.latitud, hecho.ubicacion.longitud])
                .addTo(map)
                .on('click', () => {
                    if (lastSelectedMarkerId !== hecho.id) {
                        centerMapOnMarker(markers[hecho.id]);
                    }
                    lastSelectedMarkerId = hecho.id;

                    document.getElementById('panel-titulo').textContent = hecho.titulo;
                    document.getElementById('panel-descripcion').textContent = hecho.descripcion;
                    document.getElementById('panel-categoria').textContent = hecho.categoria;
                    document.getElementById('panel-fecha-suceso').textContent = formatCustomDateTime(new Date(hecho.fechaSuceso));
                    document.getElementById('panel-latitud').textContent = `${hecho.ubicacion.latitud},`;
                    document.getElementById('panel-longitud').textContent = `${hecho.ubicacion.longitud}`;
                    document.getElementById('panel-provincia').textContent = hecho.nombreProvincia;

                    const tituloEncoded = encodeURIComponent(hecho.titulo);
                    const descripcionEncoded = encodeURIComponent(hecho.descripcion);
                    const provinciaEncoded = encodeURIComponent(hecho.provincia); // Asumiendo que 'provincia' existe en el objeto
                    document.getElementById('sugerir-edicion-btn').href = `/solicitudes/formulario?titulo=${tituloEncoded}&descripcion=${descripcionEncoded}&provincia=${provinciaEncoded}`;

                    populateCarousel(hecho.multimedias);
                    showPanel();
                });
        }
    });
}

/**
 * Inicializa los gestos de deslizamiento (swipe) para el panel.
 */
function initSwipeGestures() {
    let startX = 0, startY = 0, isSwiping = false;

    document.body.addEventListener('touchstart', e => {
        if (window.innerWidth > 767) return;
        startX = e.touches[0].clientX;
        startY = e.touches[0].clientY;
        isSwiping = false;
    }, {passive: true});

    document.body.addEventListener('touchmove', e => {
        if (window.innerWidth > 767 || startX === 0) return;
        const diffX = e.touches[0].clientX - startX;
        const diffY = e.touches[0].clientY - startY;
        if (Math.abs(diffX) > Math.abs(diffY) + 10) {
            isSwiping = true;
            e.preventDefault();
        }
    }, {passive: false});

    document.body.addEventListener('touchend', e => {
        if (window.innerWidth > 767 || !isSwiping) {
            startX = 0;
            startY = 0;
            isSwiping = false;
            return;
        }
        const diffX = e.changedTouches[0].clientX - startX;
        const swipeThreshold = 70;
        const panelIsVisible = panel.classList.contains('visible');

        if (Math.abs(diffX) > swipeThreshold) {
            if (diffX > 0 && !panelIsVisible && startX < 70) {
                showPanel();
            } else if (diffX < 0 && panelIsVisible) {
                hidePanel();
            }
        }
        startX = 0;
        startY = 0;
        isSwiping = false;
    }, {passive: true});
}

/**
 * Función principal de inicialización del mapa y sus componentes.
 * @param {Object} hechosData - Los datos de los hechos pasados desde el template.
 */
function initMap(hechosData) {
    panel = document.getElementById('panel-lateral');
    panelToggleButton = document.getElementById('panel-toggle-button');

    // Inicialización del mapa Leaflet
    map = L.map('map', {zoomControl: false}).setView([-37.3, -64.1], 5);
    L.control.zoom({position: 'bottomright'}).addTo(map);
    L.tileLayer('https://wms.ign.gob.ar/geoserver/gwc/service/tms/1.0.0/capabaseargenmap@EPSG%3A3857@png/{z}/{x}/{-y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Inicialización de marcadores
    initMarkers(hechosData);

    // Listeners de eventos
    const collapsibleNavbar = document.getElementById('navbarNav');
    if (collapsibleNavbar) {
        collapsibleNavbar.addEventListener('shown.bs.collapse', adjustPanelPosition);
        collapsibleNavbar.addEventListener('hidden.bs.collapse', adjustPanelPosition);
    }
    window.addEventListener('resize', adjustPanelPosition);
    panelToggleButton.addEventListener('click', () => {
        panel.classList.contains('visible') ? hidePanel() : showPanel();
    });

    // Ajuste inicial y gestos
    adjustPanelPosition();
    initSwipeGestures();
    adjustMapHeight();
}

function adjustMapHeight() {
    const navbar = document.querySelector('.navbar.sticky-top');
    const navbarHeight = navbar ? navbar.offsetHeight : 0;

    const windowHeight = window.innerHeight;

    // Altura real disponible
    const mapHeight = windowHeight - navbarHeight;

    document.getElementById('map-container').style.height = mapHeight + 'px';

    if (map) map.invalidateSize();
}

window.addEventListener('resize', adjustMapHeight);
window.addEventListener('orientationchange', adjustMapHeight);