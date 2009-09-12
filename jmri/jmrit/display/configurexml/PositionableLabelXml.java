package jmri.jmrit.display.configurexml;

//import java.awt.geom.AffineTransform;
import java.util.List;

import jmri.configurexml.XmlAdapter;
import jmri.jmrit.catalog.NamedIcon;
import jmri.jmrit.display.PanelEditor;
import jmri.jmrit.display.PositionableLabel;
import java.awt.Color;
import java.awt.Font;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 * Handle configuration for display.PositionableLabel objects
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2002
 * @version $Revision: 1.28 $
 */
public class PositionableLabelXml implements XmlAdapter {

    public PositionableLabelXml() {
    }

    /**
     * Default implementation for storing the contents of a
     * PositionableLabel
     * @param o Object to store, of type PositionableLabel
     * @return Element containing the complete info
     */
    public Element store(Object o) {
        PositionableLabel p = (PositionableLabel)o;

        if (!p.isActive()) return null;  // if flagged as inactive, don't store

        Element element = new Element("positionablelabel");
        storeCommonAttributes(p, element);

        element.setAttribute("fixed", p.getFixed()?"true":"false");
        element.setAttribute("showtooltip", p.getShowTooltip()?"true":"false");

        storeTextInfo(p, element);
        
        if (p.isIcon() && p.getIcon()!=null) {
            NamedIcon icon = (NamedIcon)p.getIcon();
            element.setAttribute("icon", icon.getURL());
            element.setAttribute("rotate", String.valueOf(icon.getRotation()));
            element.addContent(storeIcon("icon", icon));
        }

        element.setAttribute("class", "jmri.jmrit.display.configurexml.PositionableLabelXml");
        return element;
    }

    /**
     * Store the text formatting information.
     * <p>
     * This is always stored, even if the icon isn't in text mode,
     * because some uses (subclasses) of PositionableLabel flip
     * back and forth between icon and text, and want to remember their
     * formatting.
     */
    protected void storeTextInfo(PositionableLabel p, Element element) {
        if (p.getText()!=null) element.setAttribute("text", p.getText());
        element.setAttribute("size", ""+p.getFont().getSize());
        element.setAttribute("style", ""+p.getFont().getStyle());
        if (!p.getForeground().equals(Color.black)) {
            element.setAttribute("red", ""+p.getForeground().getRed());
            element.setAttribute("green", ""+p.getForeground().getGreen());
            element.setAttribute("blue", ""+p.getForeground().getBlue());
        }
    }
    /**
     * Default implementation for storing the common contents of an Icon
     * @param element Element in which contents are stored
     */
    public void storeCommonAttributes(PositionableLabel p, Element element) {

        element.setAttribute("x", ""+p.getX());
        element.setAttribute("y", ""+p.getY());
        element.setAttribute("level", String.valueOf(p.getDisplayLevel()));
        element.setAttribute("forcecontroloff", p.getForceControlOff()?"true":"false");
    }

    public Element storeIcon(String attrName, NamedIcon icon) {

        Element element = new Element(attrName);
        element.setAttribute("url", icon.getURL());        
        element.setAttribute("rotate", String.valueOf(icon.getRotation()));
        element.setAttribute("degrees", String.valueOf(icon.getDegrees()));
        element.setAttribute("scale", String.valueOf(icon.getScale()));
/*
        AffineTransform t = icon.getTransform();
        if (t!=null) {
            Element elem = new Element("transform");
            elem.setAttribute("m00", ""+t.getScaleX());
            elem.setAttribute("m01", ""+t.getShearX());
            elem.setAttribute("m02", ""+t.getTranslateX());
            elem.setAttribute("m10", ""+t.getShearY());
            elem.setAttribute("m11", ""+t.getScaleY());
            elem.setAttribute("m12", ""+t.getTranslateY());
            element.addContent(elem);
        }
        */
        return element;
    }
    
    public boolean load(Element element) {
        log.error("Invalid method called");
        return false;
    }

    /**
     * Create a PositionableLabel, then add to a target JLayeredPane
     * @param element Top level Element to unpack.
     * @param o  PanelEditor as an Object
     */
    public void load(Element element, Object o) {
        // create the objects
        PanelEditor p = (PanelEditor)o;
        PositionableLabel l = null;
        if (element.getAttribute("text")!=null) {
            l = new PositionableLabel(element.getAttribute("text").getValue());

            loadTextInfo(l, element);
        
        } else if (element.getAttribute("icon")!=null) {
            String name = element.getAttribute("icon").getValue();
            NamedIcon icon = NamedIcon.getIconByName(name);
            l = new PositionableLabel(icon);
            try {
                Attribute a = element.getAttribute("rotate");
                if (a!=null) {
                    int rotation = element.getAttribute("rotate").getIntValue();
                    icon.setRotation(rotation, l);
                }
            } catch (org.jdom.DataConversionException e) {}
            NamedIcon nIcon = loadIcon( l,"icon", element);
            if (nIcon!=null) {
                l.setIcon(nIcon);
            } else {
                l.setIcon(icon);
            }
        }
        if(l==null){
        	log.error("PositionableLabel is null!");
        	return;
        }
        loadCommonAttributes(l, PanelEditor.LABELS.intValue(), element);

        Attribute a = element.getAttribute("fixed");
        if ( (a!=null) && a.getValue().equals("true"))
            l.setFixed(true);
        else
            l.setFixed(false);
            
        a = element.getAttribute("showtooltip");
        if ( (a!=null) && a.getValue().equals("false"))
            l.setShowTooltip(false);
        else
            l.setShowTooltip(true);
            
        l.setSize(l.getPreferredSize().width, l.getPreferredSize().height);
        p.putLabel(l);
    }

    protected void loadTextInfo(PositionableLabel l, Element element) {
        Attribute a = element.getAttribute("size");
        try {
            if (a!=null) l.setFontSize(a.getFloatValue());
        } catch (DataConversionException ex) {
            log.warn("invalid size attribute value");
        }
        a = element.getAttribute("style");
        try {
            if (a!=null) l.setFontStyle(a.getIntValue(), Font.BOLD);  // label is created bold, so drop bold
        } catch (DataConversionException ex) {
            log.warn("invalid style attribute value");
        }

        // set color if needed
        try {
            int red = element.getAttribute("red").getIntValue();
            int blue = element.getAttribute("blue").getIntValue();
            int green = element.getAttribute("green").getIntValue();
            l.setForeground(new Color(red, green, blue));
        } catch ( org.jdom.DataConversionException e) {
            log.warn("Could not parse color attributes!");
        } catch ( NullPointerException e) {  // considered normal if the attributes are not present
        }
    
    }
	public void loadCommonAttributes(PositionableLabel l, int defaultLevel, Element element) {

        Attribute a = element.getAttribute("forcecontroloff");
        if ( (a!=null) && a.getValue().equals("true"))
            l.setForceControlOff(true);
        else
            l.setForceControlOff(false);
        
        // find coordinates
        int x = 0;
        int y = 0;
        try {
            x = element.getAttribute("x").getIntValue();
            y = element.getAttribute("y").getIntValue();
        } catch ( org.jdom.DataConversionException e) {
            log.error("failed to convert positional attribute");
        }
        l.setLocation(x,y);

        // find display level
        int level = defaultLevel;
        try {
            level = element.getAttribute("level").getIntValue();
        } catch ( org.jdom.DataConversionException e) {
            log.warn("Could not parse level attribute!");
        } catch ( NullPointerException e) {  // considered normal if the attribute not present
        }
        l.setDisplayLevel(level);
    }


    @SuppressWarnings("unchecked")
	public NamedIcon loadIcon(PositionableLabel l, String attrName, Element element) {
        NamedIcon icon = null;
        List<Element> iconList = element.getChildren(attrName);
        if (log.isDebugEnabled()) log.debug("Found "+iconList.size()+" "+attrName+" objects");
        if (iconList.size()>0) {
            Element elem = iconList.get(0);
            String name = elem.getAttribute("url").getValue();
            icon = NamedIcon.getIconByName(name);
            try {
                Attribute a = elem.getAttribute("rotate");
                if (a!=null) {
                    int rotation = a.getIntValue();
                    icon.setRotation(rotation, l);
                }
                a = elem.getAttribute("degrees");
                if (a!=null) {
                    int deg = a.getIntValue();
                    double scale = 1.0;
                    a =  elem.getAttribute("scale");
                    if (a!=null)
                    {
                        scale = elem.getAttribute("scale").getDoubleValue();
                    }
                    icon.setLoad(deg, scale, l);
                }
            } catch (org.jdom.DataConversionException dce) {}
            /*
            List<Element> coordList = elem.getChildren("transform");
            if (coordList.size()>0) {
                Element e = coordList.get(0);
                try {
                    AffineTransform t = new AffineTransform(e.getAttribute("m00").getDoubleValue(),
                                                            e.getAttribute("m10").getDoubleValue(),
                                                            e.getAttribute("m01").getDoubleValue(),
                                                            e.getAttribute("m11").getDoubleValue(),
                                                            e.getAttribute("m02").getDoubleValue(),
                                                            e.getAttribute("m12").getDoubleValue());
                    icon.setTransform(t);
                    icon.initFromLoad();
                    //icon.transformImage(icon.getIconWidth(), icon.getIconHeight(), t, l);
                } catch (org.jdom.DataConversionException dce) {}
            } else {
            log.debug("icon \""+attrName+"\" for \""+l.getName()+"\" has no transform.");
            }
            */
        } else {
            log.debug("icon \""+attrName+"\" for \""+l.getName()+"\" not found.");
        }
        return icon;
    }
    
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PanelEditorXml.class.getName());

}