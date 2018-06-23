<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<!-- Standard documents -->
	<xsl:param name="style-doc" select="'styles.xml'" />
	<xsl:variable name="styles" select="document($style-doc)" />

	<!-- UK -->
	<xsl:attribute-set name="UkPageSize"
		use-attribute-sets="A4 Helvetica Non-print-margins" />
	<xsl:variable name="ail-regions-first">
		<fo:region-body region-name="body" border="0mm"
			margin-bottom="10mm" margin-top="25mm" margin-left="0mm"
			margin-right="5mm" padding="0mm" background-repeat="no-repeat"
			background-position-horizontal="center" background-position-vertical="center">
			<xsl:attribute name="background-image">
                <xsl:value-of select="/documentData/watermark/text()" />
            </xsl:attribute>
		</fo:region-body>
		<fo:region-before region-name="first-before" extent="40mm" />
		<fo:region-after region-name="first-after" extent="5mm" />
		<fo:region-start region-name="first-start" extent="0mm" />
		<fo:region-end region-name="first-end" extent="0mm" />
	</xsl:variable>

	<xsl:variable name="ail-regions-blank">
		<fo:region-body region-name="body" border="0mm"
			margin-bottom="10mm" margin-top="25mm" margin-left="0mm"
			margin-right="5mm" padding="0mm" background-repeat="no-repeat"
			background-position-horizontal="center" background-position-vertical="center">
			<xsl:attribute name="background-image">
                <xsl:value-of select="/documentData/watermark/text()" />
            </xsl:attribute>
		</fo:region-body>
		<fo:region-before region-name="blank-before" extent="40mm" />
		<fo:region-after region-name="blank-after" extent="5mm" />
		<fo:region-start region-name="blank-start" extent="0mm" />
		<fo:region-end region-name="blank-end" extent="0mm" />
	</xsl:variable>

	<xsl:variable name="ail-regions-rest-right">
		<fo:region-body region-name="body" border="0mm"
			margin-bottom="10mm" margin-top="25mm" margin-left="0mm"
			margin-right="5mm" padding="0mm" background-repeat="no-repeat"
			background-position-horizontal="center" background-position-vertical="center">
			<xsl:attribute name="background-image">
                <xsl:value-of select="/documentData/watermark/text()" />
            </xsl:attribute>
		</fo:region-body>
		<fo:region-before region-name="right-before" extent="25mm" />
		<fo:region-after region-name="right-after" extent="5mm" />
		<fo:region-start region-name="right-start" extent="0mm" />
		<fo:region-end region-name="right-end" extent="5mm" />
	</xsl:variable>

	<xsl:variable name="ail-regions-rest-left">
		<fo:region-body region-name="body" border="0mm"
			margin-bottom="10mm" margin-top="25mm" margin-left="0mm"
			margin-right="5mm" padding="0mm" background-repeat="no-repeat"
			background-position-horizontal="center" background-position-vertical="center">
			<xsl:attribute name="background-image">
                <xsl:value-of select="/documentData/watermark/text()" />
            </xsl:attribute>
		</fo:region-body>
		<fo:region-before region-name="left-before" extent="25mm" />
		<fo:region-after region-name="left-after" extent="5mm" />
		<fo:region-start region-name="left-start" extent="5mm" />
		<fo:region-end region-name="left-end" extent="0mm" />
	</xsl:variable>

	<xsl:variable name="UkLayout">
		<!-- page sequences -->
		<fo:page-sequence-master master-name="all-pages">
			<fo:repeatable-page-master-alternatives>
				<fo:conditional-page-master-reference
					master-reference="first-page" page-position="first" />
				<fo:conditional-page-master-reference
					master-reference="other-pages-right" odd-or-even="odd" />
				<fo:conditional-page-master-reference
					master-reference="other-pages-left" odd-or-even="even" />
				<fo:conditional-page-master-reference
					master-reference="blank-pages" blank-or-not-blank="blank" />
			</fo:repeatable-page-master-alternatives>
		</fo:page-sequence-master>

		<fo:simple-page-master xsl:use-attribute-sets="UkPageSize"
			master-name="first-page">
			<xsl:copy-of select="$ail-regions-first" />
		</fo:simple-page-master>
		<fo:simple-page-master xsl:use-attribute-sets="UkPageSize"
			master-name="other-pages-right">
			<xsl:copy-of select="$ail-regions-rest-right" />
		</fo:simple-page-master>
		<fo:simple-page-master xsl:use-attribute-sets="UkPageSize"
			master-name="other-pages-left">
			<xsl:copy-of select="$ail-regions-rest-left" />
		</fo:simple-page-master>
		<fo:simple-page-master xsl:use-attribute-sets="UkPageSize"
			master-name="blank-pages">
			<xsl:copy-of select="$ail-regions-blank" />
		</fo:simple-page-master>
	</xsl:variable>

	<!-- Page sizes -->
	<xsl:attribute-set name="A4">
		<xsl:attribute name="page-height">297mm</xsl:attribute>
		<xsl:attribute name="page-width">210mm</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="Portrait">
		<xsl:attribute name="size">portrait</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="Landscape">
		<xsl:attribute name="size">portrait</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="Legal">
		<xsl:attribute name="page-height">279mm</xsl:attribute>
		<xsl:attribute name="page-width">216mm</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="LegalA4">
		<xsl:attribute name="page-height">279mm</xsl:attribute>
		<xsl:attribute name="page-width">210mm</xsl:attribute>
	</xsl:attribute-set>

	<!-- Non printable margins -->
	<xsl:attribute-set name="Non-print-margins">
		<xsl:attribute name="margin-bottom">7mm</xsl:attribute>
		<xsl:attribute name="margin-left">7mm</xsl:attribute>
		<xsl:attribute name="margin-right">7mm</xsl:attribute>
		<xsl:attribute name="margin-top">7mm</xsl:attribute>
	</xsl:attribute-set>

	<!-- Fonts -->
	<xsl:attribute-set name="Helvetica">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="Times">
		<xsl:attribute name="font-family">Times</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="Courier">
		<xsl:attribute name="font-family">Courier</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="base-font"
		use-attribute-sets="Helvetica">
		<xsl:attribute name="color">
            <xsl:value-of select="$font-colour" />
        </xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="B">
		<xsl:attribute name="font-weight">bold</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="U">
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="I">
		<xsl:attribute name="font-style">italic</xsl:attribute>
	</xsl:attribute-set>

	<!-- XHTML Styles -->
	<xsl:attribute-set name="H1" use-attribute-sets="base-font B">
		<xsl:attribute name="font-size">18pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="H2" use-attribute-sets="base-font B">
		<xsl:attribute name="font-size">14pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="H3" use-attribute-sets="base-font B">
		<xsl:attribute name="font-size">14pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="H4" use-attribute-sets="base-font">
		<xsl:attribute name="font-size">12pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="footer" use-attribute-sets="base-font">
		<xsl:attribute name="font-size">8pt</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="header" use-attribute-sets="H1">
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="normal-font"
		use-attribute-sets="base-font">
		<xsl:attribute name="font-size">10pt</xsl:attribute>
		<xsl:attribute name="color">
            <xsl:value-of select="$font-colour" />
        </xsl:attribute>
	</xsl:attribute-set>

	<!-- table layouts -->
	<xsl:attribute-set name="base-table-layout">
		<xsl:attribute name="table-layout">fixed</xsl:attribute>
		<xsl:attribute name="space-after.optimum">0mm</xsl:attribute>
		<xsl:attribute name="space-before.optimum">0mm</xsl:attribute>
		<xsl:attribute name="width">100%</xsl:attribute>
		<xsl:attribute name="padding">0mm</xsl:attribute>
	</xsl:attribute-set>

	<!-- Standard FO utilities -->
	<!-- Page Count -->
	<xsl:variable name="fo-page-count">
		<fo:block>
			<fo:block id="TVLP" />
		</fo:block>
	</xsl:variable>

	<xsl:variable name="fo-pagebreak">
		<fo:block break-after="page" keep-with-next="auto">
			<fo:leader leader-pattern="space" />
		</fo:block>
	</xsl:variable>

	<xsl:variable name="fo-blankline">
		<fo:block>
			<fo:leader leader-pattern="space" />
		</fo:block>
	</xsl:variable>

	<xsl:template match="hr">
		<fo:block text-align-last="justify">
			<fo:leader leader-pattern="rule" />
		</fo:block>
	</xsl:template>

	<xsl:template match="h1">
		<fo:inline xsl:use-attribute-sets="H1">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="h2">
		<fo:inline xsl:use-attribute-sets="H2">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="h3">
		<fo:inline xsl:use-attribute-sets="H3">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="h4">
		<fo:inline xsl:use-attribute-sets="H4">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="u">
		<fo:inline xsl:use-attribute-sets="U">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="i">
		<fo:inline xsl:use-attribute-sets="I">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="em">
		<fo:inline xsl:use-attribute-sets="I">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="strong">
		<fo:inline xsl:use-attribute-sets="B">
			<xsl:apply-templates select="node()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="b">
		<xsl:choose>
			<xsl:when test="not(parent::p/@title) or  parent::p/@title!=text()">
				<fo:inline xsl:use-attribute-sets="B">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&#x00A0;&#x00A0;&#x00A0;&#x00A0;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="p">
		<xsl:apply-templates select="node()" />
		<xsl:call-template name="br" />
	</xsl:template>

	<xsl:template match="c">
		<fo:block text-align="center">
			<xsl:apply-templates select="node()" />
		</fo:block>
	</xsl:template>

	<xsl:template match="br">
		<xsl:call-template name="br" />
	</xsl:template>

	<xsl:template name="br">
		<xsl:text>&#x00A0;</xsl:text>
	</xsl:template>

	<xsl:template match="font-size">
		<xsl:choose>
			<!-- If a value ending in 'pt' -->
			<xsl:when test="substring(@value, string-length(@value)-2, 2)='pt'">
				<fo:inline>
					<xsl:attribute name="font-size">
                        <xsl:value-of select="@value" />
                    </xsl:attribute>
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:otherwise>
				<fo:inline font-size="10pt">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="font-variant">
		<xsl:choose>
			<xsl:when test="@value=&apos;small-caps&apos;">
				<fo:inline font-variant="small-caps">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:otherwise>
				<fo:inline font-variant="normal">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="font-colour">
		<xsl:choose>
			<xsl:when test="@value='red'">
				<fo:inline color="red">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:when test="@value='grey'">
				<fo:inline color="grey">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:when test="@value='blue'">
				<fo:inline color="blue">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:when test="@value='black'">
				<fo:inline color="black">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:when test="@value='green'">
				<fo:inline color="green">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:when test="starts-with(@value,'#')">
				<fo:inline>
					<xsl:attribute name="color">
                        <xsl:value-of select="@value" />
                    </xsl:attribute>
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:when>
			<xsl:otherwise>
				<fo:inline color="black">
					<xsl:apply-templates select="node()" />
				</fo:inline>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="email">
		<fo:basic-link hyphenate="false" xsl:use-attribute-sets="mail-to">
			<xsl:value-of select="." />
		</fo:basic-link>
	</xsl:template>

	<xsl:attribute-set name="website">
		<xsl:attribute name="external-destination">
            <xsl:value-of select="." />
        </xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="mail-to" use-attribute-sets="website">
		<xsl:attribute name="external-destination">mailto:<xsl:value-of
			select="." /></xsl:attribute>
	</xsl:attribute-set>

	<xsl:template name="insert-image">
		<xsl:param name="source" />
		<xsl:param name="c-width" select="'80px'" />
		<xsl:param name="c-height" select="'80px'" />
		<xsl:if test="$source and $source!=''">
			<fo:external-graphic>
				<xsl:attribute name="content-height">
                    <xsl:value-of select="$c-height" />
                </xsl:attribute>
				<xsl:attribute name="content-width">
                    <xsl:value-of select="$c-width" />
                </xsl:attribute>
				<xsl:attribute name="src">
                    <xsl:value-of select="$source" />
                </xsl:attribute>
			</fo:external-graphic>
		</xsl:if>
	</xsl:template>

	<xsl:attribute-set name="before-border">
		<xsl:attribute name="border-before-style">solid</xsl:attribute>
		<xsl:attribute name="border-before-width">0.0mm</xsl:attribute>
		<xsl:attribute name="border-before-color">
            <xsl:value-of select="$light-colour" />
        </xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="start-border">
		<xsl:attribute name="border-start-style">solid</xsl:attribute>
		<xsl:attribute name="border-start-width">0.0mm</xsl:attribute>
		<xsl:attribute name="border-start-color">
            <xsl:value-of select="$light-colour" />
        </xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="end-border">
		<xsl:attribute name="border-end-style">solid</xsl:attribute>
		<xsl:attribute name="border-end-width">0.0mm</xsl:attribute>
		<xsl:attribute name="border-end-color">
            <xsl:value-of select="$light-colour" />
        </xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="after-border">
		<xsl:attribute name="border-after-style">solid</xsl:attribute>
		<xsl:attribute name="border-after-width">0.0mm</xsl:attribute>
		<xsl:attribute name="border-after-color">
            <xsl:value-of select="$light-colour" />
        </xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="full-border-no-colour">
		<xsl:attribute name="border-style">solid</xsl:attribute>
		<xsl:attribute name="border-width">0.2mm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="header-block">
		<xsl:attribute name="color">
            <xsl:value-of select="$font-header-colour" />
        </xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
	</xsl:attribute-set>

	<xsl:variable name="empty-cell">
		<fo:table-cell>
			<fo:block>&#x00A0;</fo:block>
		</fo:table-cell>
	</xsl:variable>

	<xsl:variable name="light-colour">
		<xsl:value-of select="$styles/Styles/Colours/@light-colour" />
	</xsl:variable>
	<xsl:variable name="dark-colour">
		<xsl:value-of select="$styles/Styles/Colours/@dark-colour" />
	</xsl:variable>
	<xsl:variable name="no-colour">
		<xsl:value-of select="$styles/Styles/Colours/@no-colour" />
	</xsl:variable>
	<xsl:variable name="font-colour">
		<xsl:value-of select="$styles/Styles/Colours/@font-colour" />
	</xsl:variable>
	<xsl:variable name="font-header-colour">
		<xsl:value-of select="$styles/Styles/Colours/@font-header" />
	</xsl:variable>
</xsl:stylesheet>
