package com.dsky.word2html.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dsky.word2html.util.FileUtil;
import com.dsky.word2html.util.WordToHtml;
import com.dsky.word2html.util.ZipUtilities;

@Controller
@RequestMapping("/")
public class Word2HtmlController {

	// The Environment object will be used to read parameters from the
	// application.properties configuration file
	@Autowired
	private Environment env;

	/**
	 * Show the index page containing the form for uploading a file.
	 */
	@RequestMapping("/")
	public String index() {
		return "index.html";
	}

	/**
	 * POST /uploadFile -> receive and locally save a file.
	 * 
	 * @param uploadfile
	 *            The uploaded file as Multipart file parameter in the HTTP
	 *            request. The RequestParam name must be the same of the
	 *            attribute "name" in the input tag with type file.
	 * 
	 * @return An http OK status in case of success, an http 4xx status in case
	 *         of errors.
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile) {

		try {
			// Get the filename and build the local file path
			String filename = uploadfile.getOriginalFilename();
			String fname = new File(filename).getName();
			String directory = env.getProperty("paths.uploadedWordFiles");
			File dirFile = new File(directory);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			String filepath = Paths.get(directory, fname).toString();

			// Save the file locally
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			stream.write(uploadfile.getBytes());
			stream.close();
			String timeStr = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
			int extIndex = fname.lastIndexOf('.');
			String filePrefix = fname.substring(0, extIndex);
			String htmlPath = WordToHtml.wordToHtml(filepath, directory, filePrefix + "_" + timeStr);
			String htmlDir = new File(htmlPath).getParent();
			String zipFile = ZipUtilities.compressDirectory(htmlDir, directory);
			FileUtil.deleteDir(htmlDir);
			FileUtil.deleteDir(filepath);
			Map<String,String> rsMap = new HashMap<String,String>();
			rsMap.put("filename", zipFile);
			return new ResponseEntity<>(rsMap, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	} 
	
	@RequestMapping(value = "/files/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource resource = null;
		try {
			String directory = env.getProperty("paths.uploadedWordFiles");
			File file = new File(directory + File.separator + filename);
			resource = new InputStreamResource(new FileInputStream(file));
			//设置下载文件名
			filename = URLEncoder.encode(file.getName(), "UTF-8");
			return ResponseEntity
					.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
					.header("charset", "utf-8")
					.body(resource);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};
		return null;
    }

} // class MainController