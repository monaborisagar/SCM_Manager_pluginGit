 

package com.logilite.scm.plugin;

//~--- non-JDK imports --------------------------------------------------------

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//~--- JDK imports ------------------------------------------------------------
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sonia.scm.SCMContextProvider;

import com.google.inject.Inject;

/**
 * Sample RESTful WebService endpoint. 
 * This sample resource is available at /api/rest/sample/hello
 */
@Path("sample/hellos")
public class HelloResource
{

  /**
   * Constructs a new HelloResource and injects all required dependencies.
   *
   *
   * @param securityContextProvider SCM-Manager context
   */
  @Inject
  public HelloResource(SCMContextProvider context)
  {
    this.context = context;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns a hello message
   *
   *
   * @return hello message
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getJameHelloMessage()
  {
	  DocumentBuilderFactory icFactory=DocumentBuilderFactory.newInstance();
	  
	  DocumentBuilder icBuilder;
	  try {
          icBuilder = icFactory.newDocumentBuilder();
          Document doc = icBuilder.newDocument();
          Element mainRootElement = doc.createElementNS("http://crunchify.com/CrunchifyCreateXMLDOM", "Companies");
          doc.appendChild(mainRootElement);

          // append child elements to root element
          mainRootElement.appendChild(getCompany(doc, "1", "Paypal", "Payment", "1000"));
          mainRootElement.appendChild(getCompany(doc, "2", "eBay", "Shopping", "2000"));
          mainRootElement.appendChild(getCompany(doc, "3", "Google", "Search", "3000"));

          // output DOM XML to console 
          Transformer transformer = TransformerFactory.newInstance().newTransformer();
          transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
          DOMSource source = new DOMSource(doc);
          StreamResult console = new StreamResult(System.out);
          transformer.transform(source, console);

          System.out.println("\nXML DOM Created Successfully..");

      } catch (Exception e) {
          e.printStackTrace();
      }
	  
	/*  File hgRepoDir=new File(context.getBaseDirectory().getAbsoluteFile(),"repositories/hg");
	  System.out.println("HG REPO DIR : " + hgRepoDir.getAbsoluteFile());
	 
	  SimpleCommand myCmd=new SimpleCommand("cmd","/c","dir");
	  myCmd.setWorkDirectory(hgRepoDir);
	  
	 try {
		System.out.println("Output : " +  myCmd.execute().getOutput());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	 
	 return null;
  }

  private static Node getCompany(Document doc, String id, String name, String age, String role) {
      Element company = doc.createElement("Company");
      company.setAttribute("id", id);
      company.appendChild(getCompanyElements(doc, company, "Name", name));
      company.appendChild(getCompanyElements(doc, company, "Type", age));
      company.appendChild(getCompanyElements(doc, company, "Employees", role));
      return company;
  }
  //utility method to create text node
  private static Node getCompanyElements(Document doc, Element element, String name, String value) {
      Element node = doc.createElement(name);
      node.appendChild(doc.createTextNode(value));
      return node;
  }
  //~--- fields ---------------------------------------------------------------

  /** SCM-Manager context */
  private SCMContextProvider context;
}
