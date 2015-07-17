package bauer.neax.pbxaccess.pages;

import java.util.Date;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class CkDateTime {
    @Persist
    @Property
    private Date actualDate1;
}
