/*
 * Copyright 2017 flow.ci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flow.plugins.gitclone;

import com.flow.plugins.gitclone.exception.PluginException;
import com.flow.plugins.gitclone.util.HttpClient;
import com.flow.plugins.gitclone.util.HttpResponse;
import com.flow.plugins.gitclone.util.ZipUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author yh@fir.im
 */
public class RsaHelper {

    private final static Logger LOGGER = LoggerFactory.build(RsaHelper.class);

    private final static String RSA_FOLDER = "rsa";

    private final static String RSA_PRIVATE_NAME = "id_rsa";

    private final static String RSA_PUBLIC_NAME = "id_rsa.pub";

    public RsaHelper() {
    }

    public void downloadRsaAndUnzip(String url, Path destPath) {
        LOGGER.trace("Start download Rsa from url - " + url);
        HttpClient.build(url).get().bodyAsStream((HttpResponse<InputStream> item) -> {
            try {

                Files.createDirectories(Paths.get(destPath.toString(), RSA_FOLDER));

                if (Objects.isNull(item.getBody())) {
                    LOGGER.trace("Not found Zip");
                    return;
                }

                ZipUtil.readZipFile(item.getBody(), Paths.get(destPath.toString(), RSA_FOLDER));

            } catch (IOException e) {
                throw new PluginException("Download Rsa error " + e.getMessage());
            }
        });
        LOGGER.trace("Finish download Rsa from url - " + url);
    }

    public Path privateKeyPath(Path destPath) {
        Path path = Paths.get(destPath.toString(), RSA_FOLDER, RSA_PRIVATE_NAME);
        if (!path.toFile().exists()) {
            return null;
        }
        return path;
    }

    public Path publicKeyPath(Path destPath) {
        Path path = Paths.get(destPath.toString(), RSA_FOLDER, RSA_PUBLIC_NAME);
        if (!path.toFile().exists()) {
            return null;
        }
        return path;
    }
}
