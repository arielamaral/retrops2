# 📋 Requisitos para Compilar RETROps2 no macOS

## ✅ O que você JÁ TEM instalado:

1. ✅ **Java 17** (OpenJDK 17.0.17) - OK!
2. ✅ **Android NDK 29.0.14206865** - OK!
3. ✅ **Android SDK** em `/Users/ariel/Library/Android/sdk` - OK!

## ❌ O que está FALTANDO:

1. ❌ **Gradle** (não encontrado no PATH)
2. ❌ **CMake** (não encontrado no PATH)
3. ⚠️ **Android SDK completo** (precisa verificar componentes)

---

## 🛠️ Como Instalar os Componentes Faltantes

### 1️⃣ Instalar Homebrew (se ainda não tiver)

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### 2️⃣ Instalar CMake

```bash
brew install cmake
```

Ou via Android Studio:
1. Abra **Android Studio**
2. Vá em **Settings** (⌘,)
3. **Appearance & Behavior** → **System Settings** → **Android SDK**
4. Aba **SDK Tools**
5. Marque **CMake** (versão 3.22.1 ou superior)
6. Clique em **Apply**

### 3️⃣ Gradle (NÃO precisa instalar separadamente!)

O projeto já vem com o **Gradle Wrapper** (`./gradlew`), então você **não precisa** instalar o Gradle globalmente. O wrapper baixa a versão correta automaticamente.

### 4️⃣ Completar Android SDK via Android Studio

Abra **Android Studio** → **Settings** → **Android SDK**:

#### SDK Platforms (aba):
- [x] **Android 14.0 (UpsideDownCake) - API Level 36** (ou superior)
- [x] **Android SDK Platform 36**

#### SDK Tools (aba):
- [x] **Android SDK Build-Tools** (versão 36 ou superior)
- [x] **NDK (Side by side)** - versão **29.0.14206865** ✅ (você já tem!)
- [x] **CMake** - versão **3.22.1** ou superior
- [x] **Android SDK Command-line Tools (latest)**
- [x] **Android SDK Platform-Tools**
- [x] **Android Emulator** (opcional, só se quiser testar em emulador)

---

## 🔧 Configurar Variáveis de Ambiente

Adicione ao seu `~/.zshrc` (ou `~/.bashrc` se usar bash):

```bash
# Java
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Android SDK
export ANDROID_HOME=$HOME/Library/Android/sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME

# Android NDK
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/29.0.14206865
export NDK_HOME=$ANDROID_NDK_HOME

# Adicionar ferramentas ao PATH
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/emulator
```

Depois execute:
```bash
source ~/.zshrc
```

---

## 🔍 Verificar se Tudo Está Instalado Corretamente

Execute estes comandos para verificar:

```bash
# 1. Verificar Java
java -version
# Deve mostrar: openjdk version "17.x.x"

# 2. Verificar Android SDK
echo $ANDROID_HOME
# Deve mostrar: /Users/ariel/Library/Android/sdk

# 3. Verificar NDK
echo $ANDROID_NDK_HOME
# Deve mostrar: /Users/ariel/Library/Android/sdk/ndk/29.0.14206865
ls $ANDROID_NDK_HOME
# Deve listar arquivos do NDK

# 4. Verificar CMake
cmake --version
# Deve mostrar: cmake version 3.x.x

# 5. Verificar ADB (Android Debug Bridge)
adb --version
# Deve mostrar: Android Debug Bridge version x.x.x

# 6. Verificar Gradle Wrapper
cd /Users/ariel/Documents/github/retrops2
./gradlew --version
# Deve mostrar informações do Gradle
```

---

## 📦 Dependências do Projeto (Já Incluídas)

Estas dependências **já vem com o projeto**, não precisa instalar:

- ✅ Gradle Wrapper (`./gradlew`)
- ✅ PCSX2 Core Libraries
- ✅ 3rd Party Libraries (harfbuzz, vixl, etc.)
- ✅ React Native (opcional, apenas se compilar com `-PenableRN=true`)

---

## 🚀 Ordem de Instalação Recomendada

### Passo 1: Instalar Android Studio
1. Baixe em: https://developer.android.com/studio
2. Instale e abra
3. Configure o SDK conforme listado acima

### Passo 2: Instalar CMake via Homebrew
```bash
brew install cmake
```

### Passo 3: Configurar Variáveis de Ambiente
```bash
# Editar ~/.zshrc
nano ~/.zshrc

# Adicionar as linhas de configuração (veja seção acima)

# Recarregar
source ~/.zshrc
```

### Passo 4: Verificar Instalações
Execute os comandos de verificação (seção acima)

### Passo 5: Criar local.properties
No diretório do projeto, crie `local.properties`:
```properties
sdk.dir=/Users/ariel/Library/Android/sdk
ndk.dir=/Users/ariel/Library/Android/sdk/ndk/29.0.14206865
```

### Passo 6: Tentar Compilar
```bash
cd /Users/ariel/Documents/github/retrops2
./gradlew assembleDebug
```

---

## 💾 Requisitos de Espaço em Disco

- **Android SDK completo**: ~15 GB
- **NDK**: ~1.5 GB
- **Projeto RETROps2**: ~2 GB
- **Build intermediários**: ~5 GB
- **APK final**: ~150-200 MB

**Total recomendado**: ~25 GB livres

---

## 🖥️ Requisitos de Hardware

### Mínimo:
- **RAM**: 8 GB
- **CPU**: Qualquer Intel/Apple Silicon (M1/M2/M3)
- **Disco**: 25 GB livres

### Recomendado:
- **RAM**: 16 GB ou mais
- **CPU**: Apple Silicon (M1/M2/M3) ou Intel i5+
- **Disco**: 50 GB livres (para builds múltiplos)
- **SSD**: Altamente recomendado (builds 5-10x mais rápidos)

---

## ⏱️ Tempo de Compilação Estimado

| Hardware | Primeira Compilação | Compilações Subsequentes |
|----------|---------------------|-------------------------|
| Apple M1/M2/M3 | 5-8 minutos | 2-3 minutos |
| Intel i7/i9 | 10-15 minutos | 3-5 minutos |
| Intel i5 | 15-20 minutos | 5-8 minutos |

---

## 🐛 Problemas Comuns e Soluções

### "CMake not found"
```bash
brew install cmake
# ou instale via Android Studio SDK Tools
```

### "ANDROID_HOME not set"
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
echo 'export ANDROID_HOME=$HOME/Library/Android/sdk' >> ~/.zshrc
source ~/.zshrc
```

### "NDK not found"
```bash
# Via Android Studio:
# Settings → SDK → SDK Tools → NDK (Side by side) → versão 29.0.14206865
```

### "java: invalid target release: 17"
```bash
# Configurar Java 17 como padrão
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### "Gradle daemon stopped"
```bash
# Aumentar memória do Gradle
echo 'org.gradle.jvmargs=-Xmx6g' >> gradle.properties
```

---

## 📱 Instalar APK no Dispositivo

Depois de compilar com sucesso:

### Via USB (ADB):
```bash
# 1. Conectar dispositivo via USB
# 2. Ativar "USB Debugging" no dispositivo
# 3. Verificar conexão
adb devices

# 4. Instalar APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Via Arquivo:
1. Copie `app/build/outputs/apk/debug/app-debug.apk` para seu dispositivo
2. Abra o arquivo no dispositivo
3. Permita instalação de fontes desconhecidas
4. Instale

---

## 📚 Recursos Adicionais

- **Android Studio**: https://developer.android.com/studio
- **Homebrew**: https://brew.sh
- **CMake**: https://cmake.org
- **Gradle**: https://gradle.org

---

## ✅ Checklist de Instalação

Use esta checklist para garantir que tudo está instalado:

- [ ] Android Studio instalado
- [ ] Java 17 instalado e configurado
- [ ] Android SDK Platform 36+ instalado
- [ ] Android SDK Build-Tools instalado
- [ ] Android NDK 29.0.14206865 instalado
- [ ] CMake instalado (via brew ou Android Studio)
- [ ] Variáveis de ambiente configuradas (ANDROID_HOME, NDK_HOME, JAVA_HOME)
- [ ] `local.properties` criado no projeto
- [ ] Terminal reiniciado (para carregar variáveis)
- [ ] `./gradlew --version` funciona
- [ ] `cmake --version` funciona
- [ ] `adb --version` funciona
- [ ] Pelo menos 25 GB de espaço em disco disponível

---

## 🎯 Status Atual do Seu Sistema

Com base na verificação:

| Component | Status | Versão/Local |
|-----------|--------|--------------|
| Java | ✅ OK | OpenJDK 17.0.17 |
| Android SDK | ✅ OK | ~/Library/Android/sdk |
| Android NDK | ✅ OK | 29.0.14206865 |
| CMake | ❌ Faltando | - |
| Gradle | ✅ OK | Wrapper incluído no projeto |
| SDK Platform 36 | ⚠️ Verificar | Via Android Studio |
| Build-Tools | ⚠️ Verificar | Via Android Studio |

### Próximos Passos para Você:

1. **Instalar CMake**:
   ```bash
   brew install cmake
   ```

2. **Verificar/Instalar SDK Components** via Android Studio:
   - SDK Platform 36
   - SDK Build-Tools 36
   - Marcar CMake nas SDK Tools

3. **Configurar variáveis de ambiente** no `~/.zshrc`

4. **Tentar compilar novamente**:
   ```bash
   cd /Users/ariel/Documents/github/retrops2
   ./gradlew clean
   ./gradlew assembleDebug
   ```

---

**Última atualização:** 26 de Janeiro de 2025
**Sistema:** macOS
**Projeto:** RETROps2 v0.0.1
