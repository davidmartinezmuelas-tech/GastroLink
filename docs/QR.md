# QR de Descarga Nightly

Puedes generar un QR para el enlace estable del release nightly:

- Enlace nightly: `https://github.com/davidmartinezmuelas-tech/GastroLink/releases/tag/nightly`

Opciones rapidas:

1. Generador online (sin almacenar en repo):
- `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=https://github.com/davidmartinezmuelas-tech/GastroLink/releases/tag/nightly`

2. Generar localmente con `qrencode`:

```bash
qrencode -o nightly-qr.png "https://github.com/davidmartinezmuelas-tech/GastroLink/releases/tag/nightly"
```

3. Incluirlo en documentacion:
- Guarda el PNG/SVG generado en `docs/diagrams/` o `docs/screenshots/` y enlazalo desde `README.md`.
