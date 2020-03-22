
package org.jooq.util.xml.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;


/**
 * <p>Java class for IndexColumnUsage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IndexColumnUsage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="index_catalog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="index_schema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="index_name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="table_catalog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="table_schema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="table_name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="column_name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ordinal_position" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="is_descending" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndexColumnUsage", propOrder = {

})
@SuppressWarnings({
    "all"
})
public class IndexColumnUsage implements Serializable, XMLAppendable
{

    private final static long serialVersionUID = 31300L;
    @XmlElement(name = "index_catalog")
    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String indexCatalog;
    @XmlElement(name = "index_schema")
    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String indexSchema;
    @XmlElement(name = "index_name", required = true)
    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String indexName;
    @XmlElement(name = "table_catalog")
    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String tableCatalog;
    @XmlElement(name = "table_schema")
    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String tableSchema;
    @XmlElement(name = "table_name", required = true)
    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String tableName;
    @XmlElement(name = "column_name", required = true)
    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String columnName;
    @XmlElement(name = "ordinal_position")
    protected int ordinalPosition;
    @XmlElement(name = "is_descending")
    protected Boolean isDescending;

    public String getIndexCatalog() {
        return indexCatalog;
    }

    public void setIndexCatalog(String value) {
        this.indexCatalog = value;
    }

    public String getIndexSchema() {
        return indexSchema;
    }

    public void setIndexSchema(String value) {
        this.indexSchema = value;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String value) {
        this.indexName = value;
    }

    public String getTableCatalog() {
        return tableCatalog;
    }

    public void setTableCatalog(String value) {
        this.tableCatalog = value;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String value) {
        this.tableSchema = value;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String value) {
        this.tableName = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String value) {
        this.columnName = value;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int value) {
        this.ordinalPosition = value;
    }

    /**
     * Gets the value of the isDescending property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsDescending() {
        return isDescending;
    }

    /**
     * Sets the value of the isDescending property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDescending(Boolean value) {
        this.isDescending = value;
    }

    public IndexColumnUsage withIndexCatalog(String value) {
        setIndexCatalog(value);
        return this;
    }

    public IndexColumnUsage withIndexSchema(String value) {
        setIndexSchema(value);
        return this;
    }

    public IndexColumnUsage withIndexName(String value) {
        setIndexName(value);
        return this;
    }

    public IndexColumnUsage withTableCatalog(String value) {
        setTableCatalog(value);
        return this;
    }

    public IndexColumnUsage withTableSchema(String value) {
        setTableSchema(value);
        return this;
    }

    public IndexColumnUsage withTableName(String value) {
        setTableName(value);
        return this;
    }

    public IndexColumnUsage withColumnName(String value) {
        setColumnName(value);
        return this;
    }

    public IndexColumnUsage withOrdinalPosition(int value) {
        setOrdinalPosition(value);
        return this;
    }

    public IndexColumnUsage withIsDescending(Boolean value) {
        setIsDescending(value);
        return this;
    }

    @Override
    public final void appendTo(XMLBuilder builder) {
        builder.append("index_catalog", indexCatalog);
        builder.append("index_schema", indexSchema);
        builder.append("index_name", indexName);
        builder.append("table_catalog", tableCatalog);
        builder.append("table_schema", tableSchema);
        builder.append("table_name", tableName);
        builder.append("column_name", columnName);
        builder.append("ordinal_position", ordinalPosition);
        builder.append("is_descending", isDescending);
    }

    @Override
    public String toString() {
        XMLBuilder builder = XMLBuilder.nonFormatting();
        appendTo(builder);
        return builder.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass()!= that.getClass()) {
            return false;
        }
        IndexColumnUsage other = ((IndexColumnUsage) that);
        if (indexCatalog == null) {
            if (other.indexCatalog!= null) {
                return false;
            }
        } else {
            if (!indexCatalog.equals(other.indexCatalog)) {
                return false;
            }
        }
        if (indexSchema == null) {
            if (other.indexSchema!= null) {
                return false;
            }
        } else {
            if (!indexSchema.equals(other.indexSchema)) {
                return false;
            }
        }
        if (indexName == null) {
            if (other.indexName!= null) {
                return false;
            }
        } else {
            if (!indexName.equals(other.indexName)) {
                return false;
            }
        }
        if (tableCatalog == null) {
            if (other.tableCatalog!= null) {
                return false;
            }
        } else {
            if (!tableCatalog.equals(other.tableCatalog)) {
                return false;
            }
        }
        if (tableSchema == null) {
            if (other.tableSchema!= null) {
                return false;
            }
        } else {
            if (!tableSchema.equals(other.tableSchema)) {
                return false;
            }
        }
        if (tableName == null) {
            if (other.tableName!= null) {
                return false;
            }
        } else {
            if (!tableName.equals(other.tableName)) {
                return false;
            }
        }
        if (columnName == null) {
            if (other.columnName!= null) {
                return false;
            }
        } else {
            if (!columnName.equals(other.columnName)) {
                return false;
            }
        }
        if (ordinalPosition!= other.ordinalPosition) {
            return false;
        }
        if (isDescending == null) {
            if (other.isDescending!= null) {
                return false;
            }
        } else {
            if (!isDescending.equals(other.isDescending)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((indexCatalog == null)? 0 :indexCatalog.hashCode()));
        result = ((prime*result)+((indexSchema == null)? 0 :indexSchema.hashCode()));
        result = ((prime*result)+((indexName == null)? 0 :indexName.hashCode()));
        result = ((prime*result)+((tableCatalog == null)? 0 :tableCatalog.hashCode()));
        result = ((prime*result)+((tableSchema == null)? 0 :tableSchema.hashCode()));
        result = ((prime*result)+((tableName == null)? 0 :tableName.hashCode()));
        result = ((prime*result)+((columnName == null)? 0 :columnName.hashCode()));
        result = ((prime*result)+ ordinalPosition);
        result = ((prime*result)+((isDescending == null)? 0 :isDescending.hashCode()));
        return result;
    }

}
