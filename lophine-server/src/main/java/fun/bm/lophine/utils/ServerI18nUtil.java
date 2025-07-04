package fun.bm.lophine.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import fun.bm.lophine.config.modules.optimizations.LanguageConfig;
import net.minecraft.DetectedVersion;
import net.minecraft.locale.DeprecatedTranslationsInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/*
 * authored by: Helvetica Volubi <suisuroru@blue-millennium.fun>
 * modified by: Lumine1909 <133463833+Lumine1909@users.noreply.github.com>
 * Some of diff form Leaves
 */
public class ServerI18nUtil {

    private static final Logger logger = Logger.getLogger("LangLoader");
    private static final String VERSION = DetectedVersion.BUILT_IN.name();
    private static final String BASE_PATH = "cache/lophine/" + VERSION + "/";
    private static final String manifestUrl = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final String resourceBaseUrl = "https://resources.download.minecraft.net/";

    private static String langPath;
    private static String assetsPath;
    private static String versionPath;
    private static String manifestPath;
    private static String langJsonPath;

    public static void init() {
        if (Objects.equals(LanguageConfig.lang, "en_us")) {
            return;
        }
        langPath = BASE_PATH + "lang/" + LanguageConfig.lang + ".json";
        assetsPath = BASE_PATH + "assets.json";
        versionPath = BASE_PATH + VERSION + ".json";
        manifestPath = BASE_PATH + "manifest.json";
        langJsonPath = "minecraft/lang/" + LanguageConfig.lang + ".json";
        logger.info("Starting load language: " + LanguageConfig.lang);
        CompletableFuture.runAsync(() -> loadI18n(LanguageConfig.lang, 2));
    }

    private static void loadI18n(String lang, int retryTime) {
        try {
            if (!Files.exists(Path.of(langPath))) {
                downloadLang(true);
            }
            Language.inject(createLangInstance());
            logger.info("Successfully loaded language: " + lang);
        } catch (Exception e) {
            logger.warning("Failed to load language file for " + lang + "\n" + e);
            if (retryTime > 0) {
                cleanCache();
                loadI18n(lang, retryTime - 1);
            } else {
                logger.severe("Failed to load for many times, use default lang \"en_us\" instead");
                cleanCache();
            }
        }
    }

    private static void downloadLang(boolean fetchFromAssets) throws Exception {
        JsonObject json;
        if (!Files.exists(Path.of(assetsPath)) || (json = loadJson(assetsPath)) == null) {
            if (fetchFromAssets) {
                downloadAssets(true);
                downloadLang(false);
            }
            return;
        }

        JsonObject langEntry = json.getAsJsonObject("objects").getAsJsonObject(langJsonPath);

        String hash = langEntry.get("hash").getAsString();
        if (hash == null || hash.length() < 2) {
            throw new IllegalArgumentException("Invalid hash value");
        }

        String langUrl = resourceBaseUrl + hash.substring(0, 2) + "/" + hash;
        fetchAndSave(langUrl, langPath);
    }

    private static void downloadAssets(boolean fetchFromVersion) throws Exception {
        JsonObject json;
        if (!Files.exists(Path.of(versionPath)) || (json = loadJson(versionPath)) == null) {
            if (fetchFromVersion) {
                downloadVersion(true);
                downloadAssets(false);
            }
            return;
        }

        JsonObject assetIndex = json.getAsJsonObject("assetIndex");
        String assetUrl = assetIndex.get("url").getAsString();
        fetchAndSave(assetUrl, assetsPath);
    }

    private static void downloadVersion(boolean fetchFromManifest) throws Exception {
        JsonObject json;
        if (!Files.exists(Path.of(manifestPath)) || (json = loadJson(manifestPath)) == null) {
            if (fetchFromManifest) {
                fetchAndSave(manifestUrl, manifestPath);
                downloadVersion(false);
            }
            return;
        }

        String versionUrl = null;
        for (JsonElement element : json.getAsJsonArray("versions")) {
            String id = element.getAsJsonObject().get("id").getAsString();
            String url = element.getAsJsonObject().get("url").getAsString();
            if (VERSION.equals(id)) {
                versionUrl = url;
                break;
            }
        }

        if (versionUrl == null) {
            throw new RuntimeException("Could not find version URL");
        }

        fetchAndSave(versionUrl, versionPath);
    }

    private static String createHttpResponse(String path) throws IOException, InterruptedException {
        try {
            HttpResponse<String> response;
            try (HttpClient httpClient = HttpClient.newHttpClient()) {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(path))
                        .build();

                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            int responseCode = response.statusCode();
            if (responseCode != 200) {
                logger.info("Unexpected response code: " + responseCode);
                logger.info("Response body: " + response.body());
                throw new UnsupportedEncodingException("Unexpected response code");
            } else {
                return response.body();
            }
        } catch (Exception e) {
            logger.warning("Error in getting info: " + e.getMessage());
            throw e;
        }
    }

    private static byte[] fetch(String urlString) throws IOException, InterruptedException {
        String ret = createHttpResponse(urlString);
        return ret.getBytes();
    }

    private static void fetchAndSave(String url, String savePath) throws IOException, InterruptedException {
        byte[] data = fetch(url);
        Path outputPath = Path.of(savePath);
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    private static void cleanCache() {
        try {
            FileUtils.deleteDirectory(Path.of(BASE_PATH).toFile());
        } catch (IOException e) {
            logger.severe("Cache cleanup failed: " + e);
        }
    }

    private static JsonObject loadJson(String path) {
        try {
            byte[] data = Files.readAllBytes(Paths.get(path));
            return JsonParser.parseString(new String(data)).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            logger.warning("Corrupt json file: " + e);
            throw e;
        } catch (Exception e) {
            logger.warning("Failed to load local JSON: " + e);
            return null;
        }
    }

    private static Language createLangInstance() throws IOException {
        DeprecatedTranslationsInfo deprecatedTranslationsInfo = DeprecatedTranslationsInfo.loadFromDefaultResource();
        Map<String, String> map = new HashMap<>();
        parseTranslations(map::put);
        deprecatedTranslationsInfo.applyToMap(map);
        final Map<String, String> map1 = Map.copyOf(map);
        return new Language() {
            @Override
            public @NotNull String getOrDefault(@NotNull String key, @NotNull String defaultValue) {
                return map1.getOrDefault(key, defaultValue);
            }

            @Override
            public boolean has(@NotNull String id) {
                return map1.containsKey(id);
            }

            @Override
            public boolean isDefaultRightToLeft() {
                return false;
            }

            @Override
            public @NotNull FormattedCharSequence getVisualOrder(@NotNull FormattedText text) {
                return sink -> text.visit(
                                (style, content) -> StringDecomposer.iterateFormatted(content, style, sink) ? Optional.empty() : FormattedText.STOP_ITERATION,
                                Style.EMPTY
                        )
                        .isPresent();
            }
        };
    }

    private static void parseTranslations(BiConsumer<String, String> output) throws IOException {
        Path filePath = Path.of(langPath);
        try (InputStream fileStream = Files.newInputStream(filePath)) {
            Language.loadFromJson(fileStream, output);
        } catch (NoSuchFileException noSuchFileException) {
            logger.warning("Couldn't find language file: " + langPath);
            throw noSuchFileException;
        } catch (Exception e) {
            logger.warning("Failed to load language from filesystem " + filePath + "\n" + e);
            throw e;
        }
    }
}