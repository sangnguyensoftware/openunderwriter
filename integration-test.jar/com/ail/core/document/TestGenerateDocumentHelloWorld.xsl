<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0">
    <xsl:import href="http://localhost:8080/openunderwriter-theme/documents/generic-layouts-fo.xsl"/>
    <xsl:param name="style-doc" select="'styles.xml'"/>
    <xsl:variable name="styles"
        select="document('http://localhost:8080/openunderwriter-theme/documents/styles.xml')"/>

    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <xsl:copy-of select="$UkLayout"/>
            </fo:layout-master-set>
            <fo:page-sequence format="1" initial-page-number="1" master-reference="all-pages">
                <!-- Body -->
                <fo:flow flow-name="body" border="0mm" padding="0mm">
                    <fo:block font-weight="bold" font-size="20pt">
                        <xsl:value-of select="documentData/headerData/itemData"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
