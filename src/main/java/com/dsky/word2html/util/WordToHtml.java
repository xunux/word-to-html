package com.dsky.word2html.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

/**
 * word 转换成html
 */
public class WordToHtml {

	public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
		wordToHtml("乐逗SDK_网游_Android客户端_接入指南_20170504.docx","D:\\");
//		word2003ToHtml("乐逗SDK_网游_Android客户端_接入指南_20170504.doc", "D:\\");
	}

	/**
	 * 
	 * @param fullPathName
	 * @param outBasePath
	 * @return
	 */
	public static String wordToHtml(String fullPathName, String outBasePath){
		return wordToHtml(fullPathName, outBasePath, null);
	}
	
	/**
	 * 
	 * @param fullPathName
	 * @param outBasePath
	 * @param subDir
	 * @return
	 */
	public static String wordToHtml(String fullPathName, String outBasePath, String subDir){
		if (fullPathName.toLowerCase().endsWith(".docx")) {
			return word2007ToHtml(fullPathName, outBasePath, subDir);
		}else if(fullPathName.toLowerCase().endsWith(".doc")){
			return word2003ToHtml(fullPathName, outBasePath, subDir);
		}
		return null;
	}
	
	/**
	 * 2007版本word转换成html
	 * 
	 * @param fullPathName
	 * @param outBasePath
	 * @return
	 */
	public static String word2007ToHtml(String fullPathName, String outBasePath, String subDir) {
		File f = new File(fullPathName);
		String fileName = f.getName();
		
		int extIndex = fileName.lastIndexOf('.');
		String prefix = fileName.substring(0, extIndex);
		
		if(StringUtils.isEmpty(subDir)){
			outBasePath = outBasePath + "/" + prefix + "/";
		}else{
			outBasePath = outBasePath + "/" + subDir + "/";
		}
		String htmlName = prefix + ".html";
		if (!f.exists()) {
			System.out.println("Sorry File does not Exists!");
			return null;
		}

		if (f.getName().toLowerCase().endsWith(".docx")) {
			// 1) 加载word文档生成 XWPFDocument对象
			InputStream in = null;
			OutputStream out = null;
			try {
				File outpathDir = new File(outBasePath);
				if (!outpathDir.exists()) {
					outpathDir.mkdirs();
				}
				in = new FileInputStream(f);
				File outFile = new File(outBasePath + htmlName);
				out = new FileOutputStream(outFile);
				XWPFDocument document = new XWPFDocument(in);

				// 2) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
//				XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(outpathDir));
				XHTMLOptions options = XHTMLOptions.create().URIResolver(new BasicURIResolver("."));
				options.setExtractor(new FileImageExtractor(outpathDir));
				options.setIgnoreStylesIfUnused(false);
				// options.setFragment(true);

				// 3) 将 XWPFDocument转换成XHTML
				XHTMLConverter.getInstance().convert(document, out, options);

				// 也可以使用字符数组流获取解析的内容
				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// XHTMLConverter.getInstance().convert(document, baos, options);
				// String content = baos.toString();
				// System.out.println(content);
				// baos.close();
				return outFile.getAbsolutePath();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XWPFConverterException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Enter only MS Office 2007+ files");
		}
		return null;
	}

	/**
	 * 2003版本word转换成html
	 * 
	 */
	public static String word2003ToHtml(String fullPathName, String outBasePath, String subDir) {
		File f = new File(fullPathName);
		String fileName = f.getName();
		final String imageBasepath = "word/media/";
		int extIndex = fileName.lastIndexOf('.');
		String prefix = fileName.substring(0, extIndex);
		if(StringUtils.isEmpty(subDir)){
			outBasePath = outBasePath + "/" + prefix + "/";
		}else{
			outBasePath = outBasePath + "/" + subDir + "/";
		}
		String imagepath = outBasePath + imageBasepath;
		String htmlName = prefix + ".html";
		InputStream in = null;
		OutputStream out = null;
		try {
			File outpathDir = new File(outBasePath);
			if (!outpathDir.exists()) {
				outpathDir.mkdirs();
			}
			File imgPath = new File(imagepath);
			if (!imgPath.exists()) {// 图片目录不存在则创建
				imgPath.mkdirs();
			}
			
			File outFile = new File(outBasePath + htmlName);
			in = new FileInputStream(f);
			out = new FileOutputStream(outFile);
			
			HWPFDocument wordDocument = new HWPFDocument(in);
			WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
					DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

			// 设置图片存放的位置
			wordToHtmlConverter.setPicturesManager(new PicturesManager() {
				public String savePicture(byte[] content, PictureType pictureType, String suggestedName,
						float widthInches, float heightInches) {
//					File file = new File(imagepath + suggestedName);
//					try {
//						OutputStream os = new FileOutputStream(file);
//						os.write(content);
//						os.close();
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
					return imageBasepath + suggestedName;
				}
			});
			
			// 处理图片，会在同目录下生成 image/media/ 路径并保存图片
			List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
			if (pics != null) {
				for (int i = 0; i < pics.size(); i++) {
					Picture pic = (Picture) pics.get(i);
					try {
						pic.writeImageContent(new FileOutputStream(imagepath + pic.suggestFullFileName()));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

			// 解析word文档
			wordToHtmlConverter.processDocument(wordDocument);
			Document htmlDocument = wordToHtmlConverter.getDocument();

			// 也可以使用字符数组流获取解析的内容
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// OutputStream outStream = new BufferedOutputStream(baos);

			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(out);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer serializer = factory.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "html");

			serializer.transform(domSource, streamResult);

			// 也可以使用字符数组流获取解析的内容
			// String content = baos.toString();
			// System.out.println(content);
			// baos.close();
			return outFile.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}