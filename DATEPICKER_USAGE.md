# Selector de Fechas en Crear Torneo

## Cambios Implementados

Se ha actualizado la pantalla de "Crear Torneo" para incluir selectores de calendario en lugar de campos de texto para las fechas de inicio y término del torneo.

### Características:

1. **Interfaz Mejorada**: Los campos de fecha ahora muestran un ícono de calendario y son de solo lectura
2. **Selector de Calendario**: Al hacer clic en los campos de fecha, se abre un DatePicker nativo de Material 3
3. **Formato Correcto**: Las fechas se envían al backend en el formato requerido: `YYYY-MM-DDTHH:mm:ssZ`
4. **Validación**: Se valida que ambas fechas estén seleccionadas antes de crear el torneo

### Formato de Fechas:

- **Visualización en UI**: `dd/MM/yyyy` (ej: 15/08/2025)
- **Envío al Backend**: `YYYY-MM-DDTHH:mm:ssZ` (ej: 2025-08-15T00:00:00Z)

### Archivos Modificados:

- `CreateTournamentScreen.kt`: Implementación del selector de fechas
- `DatePickerTest.kt`: Pruebas unitarias para validar el formateo de fechas

### Uso:

1. En la pantalla "Crear Torneo", los campos "Fecha de Inicio" y "Fecha de Término" ahora muestran un ícono de calendario
2. Al hacer clic en cualquiera de estos campos, se abre un selector de calendario
3. Selecciona la fecha deseada y presiona "Aceptar"
4. La fecha seleccionada se mostrará en formato legible (dd/MM/yyyy)
5. Al crear el torneo, las fechas se enviarán automáticamente en el formato correcto al backend

### Beneficios:

- **Mejor UX**: Más fácil e intuitivo seleccionar fechas
- **Menos Errores**: Elimina errores de formato manual
- **Consistencia**: Formato uniforme en toda la aplicación
- **Validación Automática**: El DatePicker solo permite fechas válidas