# TV TURBO C645

Aplicativo experimental e não oficial para Google TV/Android TV, desenhado para TCL C645.

## O que a v1 faz

- Interface 100% navegável por controle remoto.
- Mostra RAM disponível e armazenamento livre.
- Integração com Shizuku para obter identidade ADB/shell sem root.
- Botão **OTIMIZAR AGORA** executa apenas ações conservadoras:
  - `am kill-all`: encerra processos que o Android classifica como seguros para matar (cached etc.).
  - `pm trim-caches 1500M`: apara caches para buscar 1,5 GB de espaço livre.
  - reduz escalas de animação para `0.25x`.
- Não desinstala apps, não limpa dados de login e não desativa serviços do sistema.

## Requisito do modo profundo

Instalar e iniciar o Shizuku na TV. Em aparelhos sem root, o Shizuku precisa ser iniciado via ADB/Depuração sem fio e, normalmente, reiniciado após reboot completo da TV.

## Build

O projeto usa:
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- Java 17
- compileSdk 36
- Shizuku API 13.1.5

### Android Studio
Abra a pasta do projeto e execute **Build > Build APK(s)**.

### GitHub Actions
O arquivo `.github/workflows/build-apk.yml` gera `app-debug.apk` automaticamente e publica o APK como artefato do workflow.

Build automático habilitado no branch `main`.

## Observação técnica

A v1 fixa Shizuku API 13.1.5 e usa `Shizuku.newProcess` via reflexão. Essa API está marcada para futura remoção pelo projeto Shizuku. A próxima revisão deve migrar o executor para `UserService` antes de atualizar a dependência.
