/**
 * Crea un nuevo marcador en el mapa, lo almacena en el objeto `markers`
 * y le asigna el evento de clic para mostrar el panel lateral.
 * @param {object} hecho - El objeto del hecho con uuid, ubicacion, titulo, etc.
 */
function createAndStoreMarker(hecho) {
    if (!hecho || !hecho.ubicacion || !markers) {
        return null;
    }

    const newMarker = L.marker([hecho.ubicacion.latitud, hecho.ubicacion.longitud])
        .addTo(map)
        .on('click', () => {
            // Reutilizamos la lógica del panel lateral que ya existe en home.hbs
            document.getElementById('panel-titulo').textContent = hecho.titulo;
            document.getElementById('panel-descripcion').textContent = hecho.descripcion;
            document.getElementById('panel-categoria').textContent = hecho.categoria;
            document.getElementById('panel-fecha-suceso').textContent = hecho.fechaSuceso; // Asumiendo que el formato ya es correcto
            document.getElementById('panel-ubicacion').textContent = `${hecho.ubicacion.latitud}, ${hecho.ubicacion.longitud}`;
            document.getElementById('panel-provincia').textContent = hecho.nombreProvincia;
            showPanel(); // Función global de home.hbs para mostrar el panel
            centerMapOnMarker(newMarker); // Función global para centrar el mapa

            // Llamamos a la nueva función global para poblar el carrusel
            populateCarousel(hecho.multimedias);
        });

    markers[hecho.id] = newMarker; // Usamos UUID para almacenar el nuevo marcador
    return newMarker;
}

$(document).ready(function () {
    const searchInput = $("form[role='search'] input[name='textoLibre']");
    const searchButton = $("#boton-buscar");

    searchInput.autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "/hechos", // Usamos el endpoint /hechos
                dataType: "json",
                data: {
                    textoLibre: request.term, // El parámetro que espera el backend
                    limite: 10
                },
                success: function (data) {
                    // Corregido: Verificar si la lista 'hechos' dentro de 'data' está vacía.
                    if (!data || !data.hechos || data.hechos.length === 0) {
                        // Si no hay resultados, enviamos un objeto especial para mostrar el mensaje
                        response([{label: "No se encontraron resultados", value: request.term, noResults: true}]);
                    } else {
                        // Corregido: Mapear sobre data.hechos, no sobre data.
                        response($.map(data.hechos, function (item) {
                            return item;
                        }));
                    }
                }
            });
        },
        minLength: 3,
        select: function (event, ui) {
            // Al seleccionar una opción, se llena el input y se envía el formulario
            const selectedHecho = ui.item;

            if (!selectedHecho.noResults && window.location.pathname === "/") {
                // Ponemos el título en la caja de búsqueda
                searchInput.val(selectedHecho.titulo);

                // Buscamos el marcador correspondiente usando el UUID
                let marker = markers[selectedHecho.id];

                // Si el marcador no existe, lo creamos dinámicamente
                if (!marker) {
                    marker = createAndStoreMarker(selectedHecho);
                }

                if (marker) {
                    // Centramos el mapa en el marcador y disparamos el evento click
                    marker.fire('click');

                    // Si el navbar está expandido (visible en móvil), lo contraemos
                    const navbarCollapse = $('#navbarNav');
                    const navbarToggler = $('.navbar-toggler');
                    if (navbarCollapse.hasClass('show')) {
                        navbarToggler.click(); // Simula un clic en el botón para colapsar el navbar
                    }
                }
            } else {
                searchInput.val(selectedHecho.titulo);
                searchButton.click();
            }
            return false; // Prevenimos que el valor por defecto (request.term) se ponga en el input
        },
        appendTo: ".search-container", // Adjuntamos el menú al contenedor de la búsqueda
    }).data("ui-autocomplete")._renderItem = function (ul, item) {
        // Si es el item de "sin resultados", lo mostramos con un estilo diferente
        if (item.noResults) {
            return $("<li class='ui-autocomplete-no-results'>")
                .append(`<div>${item.label}</div>`)
                .appendTo(ul);
        }

        // **CORRECCIÓN DE SEGURIDAD (XSS):**
        // Creamos los elementos con jQuery y usamos .text() para insertar los datos de forma segura.
        const $suggestionDiv = $("<div>").addClass("autocomplete-suggestion d-flex align-items-center");
        const $icon = $("<i>").addClass("bi bi-geo-alt-fill suggestion-icon");
        const $textContentDiv = $("<div>").addClass("suggestion-text-content");
        const $title = $("<strong>").addClass("suggestion-title").text(item.titulo);
        const $description = $("<p>").addClass("suggestion-description mb-0 text-muted")
            .attr("title", item.descripcion) // attr() es seguro para atributos
            .text(item.descripcion);

        $textContentDiv.append($title).append($description);
        $suggestionDiv.append($icon).append($textContentDiv);

        return $("<li>")
            .append($suggestionDiv)
            .appendTo(ul);
    };
});