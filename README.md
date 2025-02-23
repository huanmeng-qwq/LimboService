# LimboService

[![version](https://img.shields.io/github/v/release/YourCraftMC/LimboService)](https://github.com/YourCraftMC/LimboService/releases)
[![License](https://img.shields.io/github/license/YourCraftMC/LimboService)](https://www.gnu.org/licenses/lgpl-3.0.html)
[![workflow](https://github.com/YourCraftMC/LimboService/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/YourCraftMC/LimboService/actions/workflows/maven.yml)

🌆 A lightweight & standalone LIMBO service for Minecraft.

_This project is forked from [LOOHP's Limbo](https://github.com/LOOHP/Limbo),
but will be breaking changed and maintained by [YourCraftMC](https://github.com/YourCraftMC)._

## Usage

Download latest server jars [here](https://github.com/YourCraftMC/LimboService/releases/latest)
or other versions from [Releases](https://github.com/YourCraftMC/LimboService/releases).

1. Put downloaded server jar file at a directory where you want to run the limbo server.
    - Also you can put the world scheme file to the same directory as limbo's world.
2. Use the following command lines to start the limbo server just like any other Minecraft server jars
   ```shell
   java -Xms64M -Xmx512M -jar Limbo.jar
   ```
3. Configure settings in `config.yml` and messages in `messages.yml`.
    - Also see `allowlist.yml` for allowing/disallowing players to join.
4. Join the server with configured host and enjoy the limbo!

## Development

You can find packages from GitHub Packages.

Remember to replace `VERSION` with the version you're using.

<details>
<summary><b>Maven Dependency</b></summary>

```xml

<project>
    <repositories>

        <repository>
            <!-- Using Maven Central Repository for secure and stable updates, though synchronization might be needed. -->
            <id>maven</id>
            <name>Maven Central</name>
            <url>https://repo1.maven.org/maven2</url>
        </repository>

        <repository>
            <!-- Using GitHub dependencies for real-time updates, configuration required (recommended). -->
            <id>limboservice-repo</id>
            <name>GitHub Packages for LimboService</name>
            <url>https://maven.pkg.github.com/YourCraftMC/LimboService</url>
        </repository>

    </repositories>

    <project>
        <dependencies>
            <dependency>
                <groupId>cn.ycraft</groupId>
                <artifactId>limboservice</artifactId>
                <version>[VERSION]</version>
                <scope>compile</scope>
            </dependency>
        </dependencies>
    </project>
</project>
```

</details>

<details>
<summary><b>Gradle Dependency</b></summary>

```groovy
repositories {
    // Using Maven Central Repository for secure and stable updates, though synchronization might be needed.
    mavenCentral()

    // Using GitHub dependencies for real-time updates, configuration required (recommended).
    maven { url 'https://maven.pkg.github.com/YourCraftMC/LimboService' }
}

dependencies {
    api "cn.ycraft:limboservice:[LATEST RELEASE]"
}
```

</details>

## Dependencies

- [**MCProtocolLib**](https://github.com/GeyserMC/MCProtocolLib): Used to implement base MineCraft Server functions.
- [**adventure**](https://github.com/KyoriPowered/adventure): Used to implement message components.
- [**EasyConfiguration**](https://github.com/CarmJos/EasyConfiguration/pull/101): Used to implement configurations.
- [**EasyPlugin-Color**](https://github.com/CarmJos/EasyPlugin): Used to parse generic color codes.

For more dependencies, please see  [Dependencies](https://github.com/YourCraftMC/LimboService/network/dependencies) .

## Acknowledgements & Supports

Many thanks to [LoohpJames(@LOOHP)](https://github.com/LOOHP)
and [many other developers](https://github.com/LOOHP/Limbo/graphs/contributors) for their huge contribution to the
original project.

Many thanks to Jetbrains for kindly providing a license for us to work on this and other open-source projects.

[![](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/?from=https://github.com/CarmJos/EasyConfiguration)

This project currently is mainly maintained by the  [YourCraftMC(你的世界)](https://www.ycraft.cn) .

<img src="https://raw.githubusercontent.com/YourCraftMC/.github/refs/heads/main/imgs/text_1440p.png" alt="Team logo" width="400px">

## Open Source License

This project's source code is licensed under
the [GNU LESSER GENERAL PUBLIC LICENSE](https://www.gnu.org/licenses/lgpl-3.0.html).