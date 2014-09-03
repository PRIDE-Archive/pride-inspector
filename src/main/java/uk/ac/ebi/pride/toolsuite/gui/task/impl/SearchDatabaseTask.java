package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.DatabaseSearchTableModel;
import uk.ac.ebi.pride.toolsuite.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ThrowableEvent;
import uk.ac.ebi.pride.toolsuite.gui.search.SearchEntry;
import uk.ac.ebi.pride.toolsuite.gui.search.SearchFinder;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task to search database entries
 * <p/>
 * User: rwang
 * Date: 31/05/11
 * Time: 15:27
 */
public class SearchDatabaseTask extends TaskAdapter<Void, List<List<Object>>> {
    private static final Logger logger = LoggerFactory.getLogger(SearchDatabaseTask.class);

    private static final String DEFAULT_TASK_TITLE = "Search Results";
    private static final String DEFAULT_TASK_DESCRIPTION = "Getting Search Results";

    private static final int BATCH_SIZE = 20;

    private SearchEntry entry;
    private List<String> headers;
    private List<List<String>> valueToSearch;

    public SearchDatabaseTask() {
        this(null, null, null);
    }

    public SearchDatabaseTask(SearchEntry entry) {
        this(entry, null, null);
    }

    public SearchDatabaseTask(SearchEntry entry, List<String> headers, List<List<String>> values) {
        this.entry = entry;
        this.headers = headers;
        this.valueToSearch = values;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        // start the search
        EventBus.publish(new DatabaseSearchEvent<Void>(DatabaseSearchEvent.Status.START));

        if (valueToSearch != null) {
            // search existing results
            searchExistingResults();
        } else {
            // read database summary file
            searchDatabaseSummaryFile();
        }

        // complete the search
        EventBus.publish(new DatabaseSearchEvent<Void>(DatabaseSearchEvent.Status.COMPLETE));
        return null;
    }

    /**
     * Search within a given set of results
     * todo: to be merged with getContents() method
     */
    private void searchExistingResults() {
        // to filter
        boolean toFilter = (entry != null && entry.getTerm() != null && !"".equals(entry.getTerm().trim()));

        // SearchFinder
        SearchFinder finder = null;
        if (toFilter) {
            finder = new SearchFinder(entry, headers);
        }

        // content list
        List<List<String>> content = new ArrayList<List<String>>();

        // counter
        int cnt = 0;

        for (List<String> values : valueToSearch) {
            if (!toFilter || finder.search(values)) {
                content.add(new ArrayList<String>(values));
                cnt++;
                if (cnt == BATCH_SIZE) {
                    EventBus.publish(new DatabaseSearchEvent<List<List<String>>>(null, DatabaseSearchEvent.Status.RESULT, content));
                    cnt = 0;
                    content = new ArrayList<List<String>>();
                }
            }
        }

        EventBus.publish(new DatabaseSearchEvent<List<List<String>>>(null, DatabaseSearchEvent.Status.RESULT, content));
    }

    /**
     * Read and filter summary the file
     */
    private void searchDatabaseSummaryFile() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("metadata/database_summary.tsv");
        // input stream of the database file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input));
            // get headers
            getHeaders(reader);

            // get content
            getContents(reader);

        } catch (FileNotFoundException e) {
            String msg = "Failed to find the file which contains a list of database summaries";
            logger.error("msg", e);
            EventBus.publish(new ThrowableEvent(this, ThrowableEvent.Type.WARNING, msg, e));
        } catch (IOException e) {
            String msg = "Failed to read the file which contains a list of database summaries";
            logger.error("msg", e);
            EventBus.publish(new ThrowableEvent(this, ThrowableEvent.Type.WARNING, msg, e));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    String msg = "Failed to close the connection to the file which contains a list of database summaries";
                    logger.error("msg", e);
                    EventBus.publish(new ThrowableEvent(this, ThrowableEvent.Type.WARNING, msg, e));
                }
            }
        }
    }

    /**
     * Get headers from the file
     *
     * @param reader buffered reader
     * @throws IOException exception while reading
     */
    private void getHeaders(BufferedReader reader) throws IOException {
        String line;
        // get header
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!"".equals(line)) {
                String[] parts = line.split(Constants.TAB);
                headers = new ArrayList<String>(Arrays.asList(parts));
                break;
            }
        }
    }

    /**
     * Get contents from the file
     *
     * @param reader buffered reader
     * @throws IOException exception while reading
     */
    private void getContents(BufferedReader reader) throws IOException {
        // to filter
        boolean toFilter = (entry != null && entry.getTerm() != null && !"".equals(entry.getTerm().trim()));

        // SearchFinder
        SearchFinder finder = null;
        if (toFilter) {
            finder = new SearchFinder(entry, headers);
        }

        // content list
        List<List<Object>> content = new ArrayList<List<Object>>();

        // counter
        int cnt = 0;

        String line;
        while ((line = reader.readLine()) != null) {
            if (!"".equals(line)) {
                String[] parts = line.split(Constants.TAB, -1);
                if (!toFilter || finder.search(Arrays.asList(parts))) {
                    List<Object> rowParts = prepareRowContent(parts);
                    content.add(rowParts);
                    cnt++;
                    if (cnt == BATCH_SIZE) {
                        EventBus.publish(new DatabaseSearchEvent<List<List<Object>>>(null, DatabaseSearchEvent.Status.RESULT, content));
                        cnt = 0;
                        content = new ArrayList<List<Object>>();
                    }
                }
            }
        }

        EventBus.publish(new DatabaseSearchEvent<List<List<Object>>>(null, DatabaseSearchEvent.Status.RESULT, content));
    }

    /**
     * Prepare the content of each row
     *
     * @param parts array of string contents
     * @return List<Object>    a list of row contents
     */
    private List<Object> prepareRowContent(String[] parts) {
        List<Object> rowParts = new ArrayList<Object>();
        DatabaseSearchTableModel.TableHeader[] headers = DatabaseSearchTableModel.TableHeader.values();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part == null || "".equals(part.trim())) {
                rowParts.add(" " + Constants.NOT_AVAILABLE);
            } else if (headers[i + 1].equals(DatabaseSearchTableModel.TableHeader.EXPERIMENT_ACCESSION)) {
                rowParts.add(Integer.parseInt(part));
                rowParts.add(Integer.parseInt(part));
            } else if (headers[i + 1].equals(DatabaseSearchTableModel.TableHeader.NUMBER_OF_SPECTRA) ||
                    headers[i + 1].equals(DatabaseSearchTableModel.TableHeader.NUMBER_OF_PEPTIDE) ||
                    headers[i + 1].equals(DatabaseSearchTableModel.TableHeader.NUMBER_OF_PROTEIN)) {
                rowParts.add(Integer.parseInt(part));
            } else {
                rowParts.add(part);
            }
        }
        return rowParts;
    }
}
