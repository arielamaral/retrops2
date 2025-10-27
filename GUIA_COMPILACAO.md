# 🛠️ Guia de Compilação - RETROps2

## 📋 Pré-requisitos

### 1. Ferramentas Necessárias

#### No macOS:
```bash
# Instalar Homebrew (se não tiver)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar JDK 17
brew install openjdk@17

# Configurar JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

### 2. Android SDK e NDK

Você precisa do **Android Studio** instalado para obter:
- **Android SDK** (API Level 36 ou superior)
- **Android NDK** (versão 29.0.14206865)

#### Instalação via Android Studio:
1. Abra Android Studio
2. Vá em `Settings` (ou `Preferences` no macOS)
3. Navegue até `Appearance & Behavior` → `System Settings` → `Android SDK`
4. Na aba `SDK Platforms`:
   - Marque `Android 14.0 (API 36)` ou superior
5. Na aba `SDK Tools`:
   - Marque `NDK (Side by side)` versão `29.0.14206865`
   - Marque `CMake`
   - Marque `Android SDK Build-Tools`
6. Clique em `Apply` e aguarde o download

#### Configurar variáveis de ambiente:

**No macOS/Linux** (adicione ao `~/.zshrc` ou `~/.bashrc`):
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/29.0.14206865
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

Depois execute:
```bash
source ~/.zshrc  # ou source ~/.bashrc
```

### 3. Verificar Instalação

```bash
# Verificar Java
java -version
# Deve mostrar: openjdk version "17.x.x"

# Verificar Android SDK
echo $ANDROID_HOME
# Deve mostrar: /Users/SEU_USUARIO/Library/Android/sdk

# Verificar NDK
ls $ANDROID_NDK_HOME
# Deve mostrar arquivos do NDK

# Verificar ADB
adb --version
```

---

## 🏗️ Compilação

### Opção 1: APK Normal (SEM React Native) ⭐ RECOMENDADO

Esta é a forma mais simples e funciona melhor:

```bash
cd /Users/ariel/Documents/github/retrops2

# Limpar builds anteriores
./gradlew clean

# Compilar versão Debug
./gradlew assembleDebug

# O APK será gerado em:
# app/build/outputs/apk/debug/app-debug.apk
```

### Opção 2: APK com React Native (Experimental)

⚠️ **Atenção:** Requer configuração adicional do React Native

```bash
# 1. Instalar dependências Node.js
bun install
# ou: npm install

# 2. Compilar React Native Gradle Plugin
cd node_modules/@react-native/gradle-plugin
./gradlew build
cd ../../../

# 3. Compilar APK
./gradlew assembleDebug -PenableRN=true
```

---

## 📦 Variantes de Build

### Debug (Para testes):
```bash
./gradlew assembleDebug
```
- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Tamanho: ~150-200 MB
- Permite debugging
- Performance não otimizada

### Release (Para distribuição):
```bash
./gradlew assembleRelease
```
- APK: `app/build/outputs/apk/release/app-release-unsigned.apk`
- Tamanho: ~100-150 MB
- Performance otimizada
- **Precisa ser assinado** para instalar

---

## 🔑 Assinar APK Release

Para instalar um APK Release, você precisa assiná-lo:

### 1. Criar Keystore (primeira vez):
```bash
keytool -genkey -v -keystore retrops2-release.keystore \
  -alias retrops2 -keyalg RSA -keysize 2048 -validity 10000
```

Responda as perguntas e **guarde a senha em local seguro!**

### 2. Configurar Gradle:

Crie o arquivo `keystore.properties` na raiz do projeto:
```properties
storePassword=SUA_SENHA
keyPassword=SUA_SENHA
keyAlias=retrops2
storeFile=../retrops2-release.keystore
```

### 3. Compilar Release Assinado:
```bash
./gradlew assembleRelease
```

---

## 🚀 Instalação no Dispositivo

### Via ADB (USB):
```bash
# Conectar dispositivo via USB com USB Debugging ativado

# Verificar se dispositivo foi detectado
adb devices

# Instalar APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Ou sobrescrever versão existente
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Via Arquivo:
1. Copie o APK para seu dispositivo
2. Abra o arquivo APK no dispositivo
3. Permita instalação de fontes desconhecidas (se solicitado)
4. Instale

---

## 🐛 Problemas Comuns

### 1. "NDK not found"
**Solução:**
```bash
# Verificar se NDK está instalado
ls ~/Library/Android/sdk/ndk/

# Se não tiver a versão 29.0.14206865, instale via Android Studio
# ou via linha de comando:
sdkmanager "ndk;29.0.14206865"
```

### 2. "Unsupported class file major version"
**Causa:** Java versão errada

**Solução:**
```bash
# Verificar versão
java -version

# Deve ser Java 17. Se não for:
brew install openjdk@17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### 3. "Execution failed for task ':app:mergeDebugNativeLibs'"
**Causa:** NDK não configurado ou versão errada

**Solução:**
```bash
# Verificar NDK
echo $ANDROID_NDK_HOME

# Deve apontar para:
# /Users/SEU_USUARIO/Library/Android/sdk/ndk/29.0.14206865
```

### 4. "Could not determine the dependencies of task"
**Causa:** Cache corrompido

**Solução:**
```bash
./gradlew clean
rm -rf .gradle
rm -rf app/build
./gradlew assembleDebug
```

### 5. Build muito lento
**Solução:**
```bash
# Adicionar ao gradle.properties:
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.jvmargs=-Xmx4096m
```

---

## ⚡ Dicas de Performance

### 1. Usar Gradle Daemon:
```bash
# Adicionar ao ~/.gradle/gradle.properties
org.gradle.daemon=true
org.gradle.configureondemand=true
```

### 2. Build Paralelo:
```bash
# Adicionar ao gradle.properties do projeto:
org.gradle.parallel=true
org.gradle.workers.max=4
```

### 3. Aumentar Memória do Gradle:
```bash
# Adicionar ao gradle.properties:
org.gradle.jvmargs=-Xmx6g -XX:MaxMetaspaceSize=512m
```

---

## 📊 Estrutura de Saída

Após compilação bem-sucedida:

```
retrops2/
├── app/
│   └── build/
│       └── outputs/
│           └── apk/
│               ├── debug/
│               │   └── app-debug.apk          ← APK Debug
│               └── release/
│                   └── app-release.apk        ← APK Release (assinado)
```

---

## 🔍 Verificar APK Gerado

```bash
# Ver informações do APK
aapt dump badging app/build/outputs/apk/debug/app-debug.apk

# Ver tamanho
du -h app/build/outputs/apk/debug/app-debug.apk

# Ver conteúdo
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -E "(lib/|assets/)"
```

---

## 📝 Logs e Debug

### Durante compilação:
```bash
# Build com logs detalhados
./gradlew assembleDebug --info

# Build com stack trace em caso de erro
./gradlew assembleDebug --stacktrace

# Build com máximo de informações
./gradlew assembleDebug --debug
```

### Após instalação:
```bash
# Ver logs do app em tempo real
adb logcat | grep -i retrops2

# Salvar logs em arquivo
adb logcat > retrops2-logs.txt
```

---

## 🎯 Checklist de Compilação

- [ ] Java 17 instalado e configurado
- [ ] Android SDK instalado (API 36+)
- [ ] Android NDK 29.0.14206865 instalado
- [ ] Variáveis de ambiente configuradas (`ANDROID_HOME`, `ANDROID_NDK_HOME`)
- [ ] Projeto clonado/atualizado
- [ ] `./gradlew clean` executado
- [ ] `./gradlew assembleDebug` executado com sucesso
- [ ] APK gerado em `app/build/outputs/apk/debug/`

---

## ❓ Precisa de Ajuda?

1. Verifique os logs de compilação
2. Procure o erro específico neste guia
3. Abra uma issue com:
   - Comando executado
   - Erro completo
   - Versões de Java, Gradle, SDK e NDK

---

**Última atualização:** Janeiro 2025
**Versão do Guia:** 1.0
