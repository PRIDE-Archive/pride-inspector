package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * Labeller and transformer for the group visualization
 * 
 * @author julianu
 *
 */
public class ProteinVertexLabeller
		implements Renderer.VertexLabel<VertexObject, String>, Transformer<VertexObject, Shape>{
	
	private Map<VertexObject, Shape> shapes = new HashMap<VertexObject, Shape>();
	private RenderContext<VertexObject, String> rc;
	
	private int margin;
	
	/**
	 * 
	 * @param rc
	 * @param margin the margin to the text
	 */
	public ProteinVertexLabeller(RenderContext<VertexObject, String> rc, int margin) {
		this.rc = rc;
		this.margin = margin;
	}
	
	
	public Component prepareRenderer(RenderContext<VertexObject, String> rc,
			VertexLabelRenderer graphLabelRenderer,
			Object value, 
			boolean isSelected,
			VertexObject vertex) {
		return rc.getVertexLabelRenderer().<VertexObject>getVertexLabelRendererComponent(
				rc.getScreenDevice(),
				value, 
				rc.getVertexFontTransformer().transform(vertex),
				isSelected,
				vertex);
	}
	
	
	@Override
    public void labelVertex(RenderContext<VertexObject, String> rc,
    		Layout<VertexObject, String> layout,
    		VertexObject v, String label) {
		Graph<VertexObject, String> graph = layout.getGraph();
		
		if (rc.getVertexIncludePredicate().evaluate(Context.<Graph<VertexObject, String>, VertexObject>getInstance(graph,v)) == false) {
			return;
		}
		
		GraphicsDecorator g = rc.getGraphicsContext();
		Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(),
				label, rc.getPickedVertexState().isPicked(v), v);
		Dimension d = new Dimension(
				(int)component.getPreferredSize().getWidth(),
				(int)component.getPreferredSize().getHeight());
		
		Point2D p = layout.transform(v);
		p = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p);
		
		int x = (int)p.getX();
		int y = (int)p.getY();
		
		g.draw(component, rc.getRendererPane(), x - d.width / 2, y - d.height / 2, d.width, d.height, true);
		
		d = new Dimension(
				(int)component.getPreferredSize().getWidth() + margin*2,
				(int)component.getPreferredSize().getHeight() + margin*2);
		Rectangle bounds = new Rectangle(-d.width / 2, -d.height / 2, d.width, d.height);
		shapes.put(v, bounds);
    }
	
	
	@Override
	public Shape transform(VertexObject v) {
		Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), rc.getVertexLabelTransformer().transform(v),
				rc.getPickedVertexState().isPicked(v), v);
        Dimension size = new Dimension(
				(int)component.getPreferredSize().getWidth() + margin*2,
				(int)component.getPreferredSize().getHeight() + margin*2);
        Rectangle bounds = new Rectangle(-size.width/2 -2, -size.height/2 -2, size.width+4, size.height);
        return bounds;
	}
	
	
	@Override
	public Position getPosition() {
		// the label is always centered
		return Position.CNTR;
	}
	
	
	@Override
	public Positioner getPositioner() {
		return new Positioner() {
			public Position getPosition(float x, float y, Dimension d) {
				return Position.CNTR;
			}};
	}
	
	
	@Override
	public void setPosition(Position position) {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void setPositioner(Positioner positioner) {
		// TODO Auto-generated method stub
	}
}
