package uk.ac.ebi.pride.toolsuite.gui.component.db;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.DatabaseSearchTableModel;

import javax.swing.*;

/**
 * Data model for the search field combo box, provides a list of fields
 *
 * User: rwang
 * Date: 02/06/11
 * Time: 15:03
 */
public class FieldComboBoxModel extends DefaultComboBoxModel{

    public FieldComboBoxModel() {
        super();
        // enable annotation
        AnnotationProcessor.process(this);

        this.addElement("Any field");
        DatabaseSearchTableModel.TableHeader[] headers = DatabaseSearchTableModel.TableHeader.values();
        for (DatabaseSearchTableModel.TableHeader header : headers) {
            this.addElement(header.getHeader());
        }
    }
}
