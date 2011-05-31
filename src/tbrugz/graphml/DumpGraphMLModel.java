package tbrugz.graphml;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tbrugz.graphml.model.Edge;
import tbrugz.graphml.model.Root;
import tbrugz.graphml.model.Node;
import tbrugz.graphml.model.Stereotyped;
import tbrugz.xml.AbstractDump;
import tbrugz.xml.model.skel.Composite;
import tbrugz.xml.model.skel.Element;

public class DumpGraphMLModel extends AbstractDump {

	static Log log = LogFactory.getLog(AbstractDump.class);

	Set<String> nodeNames = new TreeSet<String>();
	
	{
		loadSnippets("graphml-snippets.properties");
	}
	
	@Override
	public void dumpModel(Element root, PrintStream out) {
		super.dumpModel(root, out);
	}

	@Override
	public void dumpModel(Element elem, int level) {
		if(elem instanceof Root) {
			outSnippet("graphml", level);
		}
		else if(elem instanceof Node) {
			Node t = (Node) elem;
			out("<node id=\""+t.getId()+"\">", level);
			outNodeContents(t, level+1);
			out("</node>", level);
			nodeNames.add(t.getId());
		}
		else if(elem instanceof Edge) {
			Edge myl = (Edge) elem;
			out("<edge source=\""+myl.getSource()+"\" target=\""+myl.getTarget()+"\">", level);
			outEdgeContents(myl, level+1);
			out("</edge>", level);
		}
		else {
			out(">> unknown element: "+elem.getClass().getName(), level);
		}
		
		if(elem instanceof Composite) {
			for(Element e: ((Composite)elem).getChildren()) {
				dumpModel(e, level+1);
			}
		}

		//end of processing
		if(elem instanceof Root) {
			out("</graph></graphml>", level);
		}
	}
	
	public String getSnippetId(Stereotyped st, String s) {
		if(st.getStereotype()!=null) {
			String news = s+"."+st.getStereotype();
			if(hasSnippet(news)) { return news; }
		}
		return s;
	}
	
	@Deprecated
	public void oldOutNodeContents(Node t, int level) {
		outSnippet(getSnippetId(t, "node"), level, t.getLabel());
	}

	public void outEdgeContents(Edge l, int level) {
		outSnippet(getSnippetId(l, "edge"), level);
	}

	public void outNodeContents(Node t, int level) {
		if(t.getStereotypeParamCount()>0) {
			String[] args = new String[t.getStereotypeParamCount()];
			for(int i=0; i<t.getStereotypeParamCount(); i++) {
				args[i] = t.getStereotypeParam(i);
			}
			outSnippet(getSnippetId(t, "node"), level, args);
		}
		else {
			outSnippet(getSnippetId(t, "node"), level, t.getLabel());
		}
	}
}
