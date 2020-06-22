/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.ioutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author mwimmer
 *
 */
public class FileTransferUtils {
	
	private static Logger log = LoggerFactory.getLogger(FileTransferUtils.class);

	public long download(URL url, String outputFilename) throws IOException {
		log.info("downloading file...");
		ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
		FileOutputStream fileOutputStream = new FileOutputStream(outputFilename);
		long bytes = fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		fileOutputStream.close();
		log.info("File downloaded: " + outputFilename);
		if (bytes <= 0) {
			log.warn("File has a size of " + bytes + " bytes. Really ok?");
		}
		return bytes;
	}
	
	public boolean upload(URI url, File file) {
		log.info("uploading file...");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(file));
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		 
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
		
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			log.info("file upload successful");
			return true;
		} else {
			log.error("file upload not successful: " + response.getStatusCode());
			return false;
		}

	}
	
	public void uploadZipped(URL url, File file) {
		try {
			
			FileOutputStream fileOut = new FileOutputStream(file.getAbsoluteFile() + ".zip");
			ZipOutputStream zout = new ZipOutputStream(fileOut);
			FileInputStream fileIn = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zout.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
	        int length;
	        while((length = fileIn.read(bytes)) >= 0) {
	        	zout.write(bytes, 0, length);
	        }
			
	        zout.closeEntry();
			zout.close();
			fileOut.close();
			fileIn.close();

			URI uri = url.toURI();
			upload(uri, new File(file.getAbsoluteFile() + ".zip"));
	        
		} catch (IOException e) {
			log.error("could not zip file", e);
		
		} catch (URISyntaxException e) {
			log.error("could not zip file", e);
		}
		
	}

}
