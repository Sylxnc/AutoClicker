# Sylxnc AutoClicker

[![Build](https://github.com/sylxnc/AutoClicker/actions/workflows/build.yml/badge.svg)](https://github.com/sylxnc/AutoClicker/actions/workflows/build.yml)

Ein schneller, plattformübergreifender Auto-Clicker von [Sylxnc](https://sylxnc.net) mit übersichtlicher Java-Swing-GUI.

Ein einfacher, plattformübergreifender Auto-Clicker mit übersichtlicher Java-Swing-GUI.

## Funktionen

- Klickintervall von 10 bis 60.000 Millisekunden
- Start/Stopp per Button oder globalem `F4`
- Beenden per globalem `F5`
- Läuft auf Windows, macOS und Linux
- Keine zusätzlichen nativen Plattform-Bibliotheken außer JNativeHook für globale Hotkeys

## Plattform-Hinweise

Die Anwendung läuft auf Windows, macOS und Linux. Für globale Hotkeys müssen unter macOS die
**Bedienungshilfen** und unter Linux gegebenenfalls entsprechende Desktop-Berechtigungen aktiviert werden.

## Voraussetzungen

- Java 17 oder neuer
- Auf macOS eventuell die Berechtigung **Bedienungshilfen** für globale Tastaturereignisse

## Starten

```bash
mvn clean package
java -jar target/sylxnc-autoclicker-1.0.0.jar
```

Auf Linux benötigt die Desktop-Umgebung möglicherweise zusätzliche Berechtigungen für globale Tastatur-Hooks.

## Entwicklung

```bash
mvn clean verify
```

## Lizenz

Dieses Projekt steht unter der MIT-Lizenz. Siehe [LICENSE](LICENSE).  


 
