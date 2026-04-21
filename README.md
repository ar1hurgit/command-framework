# Command Framework

Framework de commandes moderne pour plugins Minecraft Paper et Spigot, construit avec Gradle et Java 21.

[![JitPack](https://jitpack.io/v/ar1hurgit/command-framework.svg)](https://jitpack.io/#ar1hurgit/command-framework)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-181717?logo=github&logoColor=white)](https://github.com/ar1hurgit/command-framework)
![Paper](https://img.shields.io/badge/Paper-1.21%20%2F%201.21.1-1B9AE5)
![Spigot](https://img.shields.io/badge/Spigot-1.21%20%2F%201.21.1-ED8106)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?logo=gradle&logoColor=white)

![Commits](https://img.shields.io/github/commit-activity/m/ar1hurgit/command-framework?label=Commits&color=1B9AE5)
![Issues](https://img.shields.io/github/issues/ar1hurgit/command-framework?label=Issues&color=D73A49)
![Last Commit](https://img.shields.io/github/last-commit/ar1hurgit/command-framework?label=Last%20Commit&color=F59E0B)
![Repo Size](https://img.shields.io/github/repo-size/ar1hurgit/command-framework?label=Size&color=9CA3AF)

![Contributors](https://img.shields.io/github/contributors/ar1hurgit/command-framework?label=Contributors&color=0F766E)
![Stars](https://img.shields.io/github/stars/ar1hurgit/command-framework?label=Stars&color=EAB308)

## Overview

Command Framework permet de créer des commandes Paper/Spigot avec une API orientée annotations, un parsing automatique des arguments, la validation, la tab-completion, l'aide générée automatiquement et des points d'extension propres pour les parsers, completions et middlewares.

Le projet cible actuellement :

- Java 21
- Paper 1.21 / 1.21.1
- Spigot 1.21 / 1.21.1
- Gradle

## Features

- Déclaration de commandes via annotations : `@Command`, `@Alias`, `@Permission`, `@Description`, `@Usage`, `@Cooldown`
- Exécution et validation avec `@Execute`, `@Optional`, `@Range`, `@Suggest`
- Parsing automatique pour `String`, `int`, `double`, `boolean`, `Player`, `OfflinePlayer`, `UUID` et `Enum`
- Registry dynamique avec `register(...)` et scan de package
- Tab-completion intelligente pour joueurs, enums, suggestions annotées et sous-commandes
- Help automatique avec `/command help`
- Middlewares, métriques et extensions custom

## Compatibility

| Platform | Minecraft | Java | Status |
| --- | --- | --- | --- |
| Paper | 1.21 / 1.21.1 | 21 | Supported |
| Spigot | 1.21 / 1.21.1 | 21 | Supported |

## Installation

### Gradle

Ajoute JitPack dans ton `settings.gradle` :

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Puis ajoute la dépendance dans ton `build.gradle` :

```gradle
dependencies {
    implementation 'com.github.ar1hurgit:command-framework:v1.0.0-alpha'
}
```

### Maven

Ajoute JitPack dans ton `pom.xml` :

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Puis ajoute la dépendance :

```xml
<dependency>
    <groupId>com.github.ar1hurgit</groupId>
    <artifactId>command-framework</artifactId>
    <version>v1.0.0-alpha</version>
</dependency>
```

## Quick Start

Exemple d'utilisation simple :

```java
import me.ar1hurgit.commandframework.framework.annotation.Command;
import me.ar1hurgit.commandframework.framework.annotation.Description;
import me.ar1hurgit.commandframework.framework.annotation.Execute;
import me.ar1hurgit.commandframework.framework.annotation.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("ban")
@Permission("staff.ban")
@Description("Ban un joueur")
public final class BanCommand {

    @Execute
    public void execute(CommandSender sender, Player target, String reason) {
        sender.sendMessage("Banned " + target.getName() + " for " + reason);
    }
}
```

Dans ton plugin :

```java
import me.ar1hurgit.commandframework.framework.core.CommandFramework;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        commandFramework = CommandFramework.builder(this).build();
        commandFramework.register(new BanCommand());
    }
}
```

## API Highlights

Le framework est organisé en modules clairs :

- `framework.annotation` : annotations publiques du framework
- `framework.core` : configuration, métadonnées, métriques
- `framework.registry` : enregistrement, scan et routing
- `framework.executor` : pipeline d'exécution
- `framework.parser` : parsers d'arguments
- `framework.completion` : tab-completion extensible
- `framework.validation` : permission, cooldown, sender, ranges
- `framework.help` : génération automatique de l'aide
- `framework.context` : contexte d'exécution
- `framework.exception` : exceptions typées
- `framework.util` : utilitaires internes

## Extension

Parser custom :

```java
commandFramework.registerParser(MyType.class, context -> new MyType(context.input()));
```

Completion custom :

```java
commandFramework.registerCompletion(
    MyType.class,
    (context, parameter, input) -> List.of("alpha", "beta", "gamma")
);
```

Middleware :

```java
commandFramework.addMiddleware(new CommandMiddleware() {
    @Override
    public void beforeExecute(CommandContext context) {
        context.sender().sendMessage("Running " + context.definition().commandKey());
    }
});
```

Scan de package :

```java
commandFramework.scanAndRegister("me.example.myplugin.command");
```

## Development

Pour compiler le projet localement :

```powershell
.\gradlew.bat build
```

Pour lancer une publication locale Maven :

```powershell
.\gradlew.bat publishToMavenLocal
```

## Support

- Email : `arthurdk0805@gmail.com`
- Buy Me a Coffee : [buymeacoffee.com/ar1hurgit](https://buymeacoffee.com/ar1hurgit)

## License

MIT License
