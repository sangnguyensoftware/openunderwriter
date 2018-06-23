<?xml version="1.0" encoding="UTF-8"?>

<!-- 
 This is the default implementation of the document style service. It is only used in the absence
 of a defined styling service.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:template match="/">
        <styleDocumentArgImp xsi:type="java:com.ail.core.document.StyleDocumentArgumentImpl"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <styledDocumentRet>
                <documentData>
                    <headerData>
                        <itemData>Hello New World</itemData>
                    </headerData>
                </documentData>
            </styledDocumentRet>
        </styleDocumentArgImp>
    </xsl:template>
</xsl:stylesheet>
