window.onload = function() {
    const ui = window.ui;
    const token = 'ВАШ_JWT_ТОКЕН'; // Замените на актуальный токен или добавьте логику для его получения

    // Устанавливает токен в Swagger UI
    ui.preauthorizeApiKey("BearerAuth", "Bearer " + token);
}
