package fr.insee.vtl.model;

import java.util.Map;

/**
 * The <code>VtlResults</code> class represents the results of the evaluation of a VTL expression.
 */
public class VtlResults {

    /**
     * Returns the input bindings.
     *
     * @return The input bindings as a map between variable name and corresponding dataset.
     */
    Map<String, Dataset> getInputs() {

        return null;
    }

    /**
     * Returns the output bindings.
     *
     * @return The output bindings as a map between variable name and corresponding dataset.
     */
    Map<String, Dataset> getOutputs() {

        return null;
    }

    /**
     * Returns the metadata set produced by the evaluation.
     *
     * @return The metadata set.
     */
    Metadataset getMetadata() {

        return null;
    }
}
