package me.ooi.tinyquery;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import me.ooi.tinyquery.util.ScanUtils;

/**
 * @author jun.zhao
 */
public class XmlQuerySourceReader {
	
	private static final Map<String, String> EMPTY = new HashMap<String, String>();
	
	private static EntityResolver ignoreDtdEntityResolver = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new StringReader(""));
		}
	};
	
	private Class<?> queryInterfaces;
	public XmlQuerySourceReader(Class<?> queryInterfaces) {
		super();
		this.queryInterfaces = queryInterfaces;
	}

	/**
	 * 
	 * @param queryInterfaces xml文件和“queryInterfaces”是同目录，文件名除了扩展名是“xml”也是相同的
	 * @return Map，key：methodName；value：query
	 * @throws IOException 
	 */
	public Map<String, String> read() throws IOException {
		String xmlPath = queryInterfaces.getName().replace(".", "/")+".xml";
		InputStream is = ScanUtils.getResourceAsStream(xmlPath);
		if( is == null ) {
			return EMPTY;
		}
		
		try {
			return read(is);
		} finally {
			if( is != null ) {
				is.close();
			}
		}
	}
	
	//获取Document
	private Document getDocument(InputStream is){
		SAXReader reader = new SAXReader() ; 
		reader.setEntityResolver(ignoreDtdEntityResolver);
		Document doc = null ; 
		try {
			doc = reader.read(is) ;
		} catch (DocumentException e) {
			throw new QueryBuildException("读取["+queryInterfaces+"]失败！", e) ; 
		} 
		return doc ; 
	}
	
	/**
	 * 读取query
	 * @param is
	 * @return Map，key：methodName；value：query
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> read(InputStream is) {
		Map<String, String> ret = new HashMap<String, String>();
		Document doc = getDocument(is) ; 
		Element root = doc.getRootElement() ; 
		if( root == null ){
			throw new QueryBuildException("根节点为空！") ; 
		}
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element ele = it.next() ; 
//			if( "select".equals(ele.getName()) ){
				String methodName = ele.attributeValue("id") ; 
				ret.put(methodName, ele.getText()==null?null:ele.getText().trim());
//			}
	    }
		return ret;
	}

}
