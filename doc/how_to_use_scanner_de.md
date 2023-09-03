# 📷 QR-Code Scanner

Der Scanner liest und verifiziert die vom Generator erstellten QR-Codes.

## ⚡ Quick Start
1. Starte die *ACR Scanner* App
2. Willige ein, dass die App die Kamera benutzen darf
3. Gib den Veranstaltungsnamen und das Veranstaltungsdatum ein.
4. Beim ersten Starten muss der public key des Generators über das *Advanced* Menü importiert werden. Dafür gibt es zwei Optionen:
    - Scanne den QR-Code *public.png* den der Generator erstellt hat. Tippe dazu das Kamerasymbol neben dem public key Feld.
    - Kopiere den Inhalt der *public.pem* Datei die der Generator erstellt hat manuell in das *Public key* feld.
5. Scanne QR-Codes. Nach jedem Scan wird angezeigt ob dieser erfolgreich war oder nicht. Bei nicht erfolgreichen Scans wird außerdem der Grund für das Fehlschlagen angezeigt.
6. Um weiter zu scannen bestätige das Ergebnis mit dem *Ok* Button.

## ⚙ Konfiguration

- Du kannst jederzeit über die drei Punkte in der oberen Menüleiste zurück zur Konfigurationsansicht gelangen.
- Solltest du die App zurücksetzen wollen, geht dies über die Android Einstellungen über *Apps* -> *ACR Scanner* -> *Speicher* -> *DATEN LÖSCHEN*

## 🔍 Details

* Veranstaltungsname und Veranstaltungsdatum müssen exakt den Einstellungen entsprechen, mit denen die QR-Codes generiert wurden
* Sollte sich das Veranstaltungsdatum geändert haben, muss das ursprüngliche Datum verwendet werden.