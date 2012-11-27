package gov.va.common.xml;

/**
 * based on net.ihe.gazelle.sch.validator.util.ValidationErrorHandler
 */

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidationErrorHandler implements ErrorHandler {

    private List<ValidationException> exceptions;

    public ValidationErrorHandler() {
        exceptions = new ArrayList<ValidationException>();
    }

    public void setExceptions(List<ValidationException> exceptions) {
        this.exceptions = exceptions;
    }

    public List<ValidationException> getExceptions() {
        return exceptions;
    }

    public void error(SAXParseException exception) throws SAXException {
        if (exceptions == null) {
            exceptions = new ArrayList<ValidationException>();
        }
        exceptions.add(new ValidationException("error", exception));
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        if (exceptions == null) {
            exceptions = new ArrayList<ValidationException>();
        }
        exceptions.add(new ValidationException("fatalError", exception));
    }

    public void warning(SAXParseException exception) throws SAXException {
        if (exceptions == null) {
            exceptions = new ArrayList<ValidationException>();
        }
        exceptions.add(new ValidationException("warning", exception));
    }
}
