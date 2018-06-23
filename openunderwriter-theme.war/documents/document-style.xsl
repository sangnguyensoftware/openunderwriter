<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0" xmlns:java="java">
	<xsl:import
		href="http://localhost:8080/openunderwriter-theme/documents/generic-layouts-fo.xsl" />
	<xsl:param name="style-doc" select="'styles.xml'" />
	<xsl:variable name="styles"
		select="document('http://localhost:8080/openunderwriter-theme/documents/styles.xml')" />

	<xsl:template match="/">
		<fo:root xsl:use-attribute-sets="normal-font">
			<fo:layout-master-set>
				<xsl:copy-of select="$UkLayout" />
			</fo:layout-master-set>
			<fo:page-sequence format="1" initial-page-number="1"
				master-reference="all-pages">
				<!-- before -->
				<fo:static-content display-align="before"
					flow-name="first-before">
					<xsl:call-template name="header-template">
						<xsl:with-param name="head-left" select="//headerData/leftLogo" />
						<xsl:with-param name="head-middle" select="//headerData/itemData/text()" />
						<xsl:with-param name="head-right" select="//headerData/rightLogo" />
					</xsl:call-template>
				</fo:static-content>
				<fo:static-content display-align="before"
					flow-name="blank-before">
					<xsl:call-template name="header-template">
						<xsl:with-param name="head-middle">
							<xsl:text>This page has been left intentionally blank.</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</fo:static-content>
				<fo:static-content display-align="before"
					flow-name="right-before">
					<xsl:call-template name="header-template">
						<xsl:with-param name="head-left" select="//headerData/leftLogo" />
						<xsl:with-param name="head-middle" select="//headerData/itemData/text()" />
						<xsl:with-param name="head-right" select="//headerData/rightLogo" />
					</xsl:call-template>
				</fo:static-content>
				<fo:static-content display-align="before"
					flow-name="left-before">
					<xsl:call-template name="header-template">
						<xsl:with-param name="head-left" select="//headerData/leftLogo" />
						<xsl:with-param name="head-middle" select="//headerData/itemData/text()" />
						<xsl:with-param name="head-right" select="//headerData/rightLogo" />
					</xsl:call-template>
				</fo:static-content>
				<!-- after -->
				<fo:static-content display-align="after"
					flow-name="first-after">
					<xsl:call-template name="footer">
						<xsl:with-param name="rl" select="'centre'" />
					</xsl:call-template>
				</fo:static-content>
				<fo:static-content display-align="after"
					flow-name="right-after">
					<xsl:call-template name="footer">
						<xsl:with-param name="rl" select="'right'" />
					</xsl:call-template>
				</fo:static-content>
				<fo:static-content display-align="after"
					flow-name="left-after">
					<xsl:call-template name="footer">
						<xsl:with-param name="rl" select="'left'" />
					</xsl:call-template>
				</fo:static-content>
				<!-- Body -->
				<fo:flow flow-name="body" border="0mm" padding="0mm">
					<fo:block>
						<xsl:apply-templates select="node()" />
					</fo:block>
					<xsl:copy-of select="$fo-page-count" />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template name="header-template">
		<xsl:param name="head-left" />
		<xsl:param name="head-middle" />
		<xsl:param name="head-right" />
		<fo:table xsl:use-attribute-sets="base-table-layout">
			<fo:table-column column-width="proportional-column-width(1)" />
			<fo:table-column column-width="proportional-column-width(10)" />
			<fo:table-column column-width="proportional-column-width(1)" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="source" select="$head-left" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:choose>
							<xsl:when test="not($head-middle) or $head-middle=''">
								<fo:block>&#x00A0;</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block text-align="center" xsl:use-attribute-sets="header">
									<xsl:value-of select="$head-middle" />
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="source" select="$head-right" />
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template name="footer">
		<xsl:param name="rl" select="right" />
		<fo:table xsl:use-attribute-sets="base-table-layout">
			<fo:table-column column-width="proportional-column-width(1)" />
			<fo:table-column column-width="proportional-column-width(1)" />
			<fo:table-column column-width="proportional-column-width(1)" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<xsl:choose>
							<xsl:when test="$rl='left'">
								<fo:block text-align="left" xsl:use-attribute-sets="footer">
									Page&#x00A0;
									<fo:page-number />
									&#x00A0;of&#x00A0;
									<fo:page-number-citation ref-id="TVLP" />
								</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block>&#x00A0;</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:choose>
							<xsl:when test="$rl='centre'">
								<fo:block text-align="center" xsl:use-attribute-sets="footer">
									Page&#x00A0;
									<fo:page-number />
									&#x00A0;of&#x00A0;
									<fo:page-number-citation ref-id="TVLP" />
								</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block>&#x00A0;</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</fo:table-cell>
					<fo:table-cell>
						<xsl:choose>
							<xsl:when test="$rl='right'">
								<fo:block text-align="right" xsl:use-attribute-sets="footer">
									Page&#x00A0;
									<fo:page-number />
									&#x00A0;of&#x00A0;
									<fo:page-number-citation ref-id="TVLP" />
								</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block>&#x00A0;</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>

	</xsl:template>

	<xsl:template match="documentData">
		<xsl:for-each select="chapterData">
			<xsl:if test="position()!=1">
				<xsl:copy-of select="$fo-pagebreak" />
			</xsl:if>
			<xsl:call-template name="make-bookmark" />
			<xsl:apply-templates select="node()" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="make-bookmark">
		<xsl:param name="data" select="." />
	</xsl:template>

	<xsl:template match="block">
		<xsl:variable name="column1">
			<xsl:choose>
				<xsl:when test="ancestor::block">
					4
				</xsl:when>
				<xsl:otherwise>
					1
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="colour">
			<xsl:choose>
				<xsl:when test="ancestor::block">
					<xsl:value-of select="$no-colour" />
				</xsl:when>
				<xsl:when test="@border='false'">
					<xsl:value-of select="$no-colour" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$dark-colour" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<fo:table xsl:use-attribute-sets="base-table-layout">
			<fo:table-column column-width="proportional-column-width({$column1})" />
			<fo:table-column column-width="proportional-column-width({98-$column1*2})" />
			<fo:table-column column-width="proportional-column-width({$column1})" />

			<xsl:if test="not(ancestor::block)">
				<fo:table-header>
					<fo:table-row>
						<xsl:copy-of select="$empty-cell" />
						<fo:table-cell padding="2mm">
							<fo:block xsl:use-attribute-sets="header-block">
								<xsl:value-of select="@title" />
								&#x00A0;
							</fo:block>
						</fo:table-cell>
						<xsl:copy-of select="$empty-cell" />
					</fo:table-row>
				</fo:table-header>

				<fo:table-body>
					<fo:table-row>
						<xsl:copy-of select="$empty-cell" />
						<fo:table-cell border-color="{$colour}"
							xsl:use-attribute-sets="full-border-no-colour">
							<xsl:apply-templates select="node()" />
							&#x00A0;
						</fo:table-cell>
						<xsl:copy-of select="$empty-cell" />
					</fo:table-row>
				</fo:table-body>
			</xsl:if>

			<xsl:if test="ancestor::block">
				<fo:table-body>
					<fo:table-row>
						<xsl:copy-of select="$empty-cell" />
						<fo:table-cell border-width="0.5mm">
							<xsl:apply-templates select="node()" />
							&#x00A0;
						</fo:table-cell>
						<xsl:copy-of select="$empty-cell" />
					</fo:table-row>
				</fo:table-body>
			</xsl:if>

		</fo:table>
		<xsl:if test="not(ancestor::block)">
			<xsl:copy-of select="$fo-blankline" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="repeatingData">
		<xsl:variable name="columns">
			<xsl:value-of select="count(block[1]/itemData)" />
		</xsl:variable>
		<fo:table xsl:use-attribute-sets="base-table-layout">
			<xsl:for-each select="block[1]/itemData">
				<fo:table-column column-width="proportional-column-width(1)" />
			</xsl:for-each>
			<xsl:for-each select="block">
				<xsl:if test="@placement='header'">
					<fo:table-header>
						<fo:table-row>
							<xsl:for-each select="itemData">
								<fo:table-cell padding-start.length="1mm"
									xsl:use-attribute-sets="after-border end-border">
									<fo:block display-align="after">
										<xsl:apply-templates select="." />
										&#x00A0;
									</fo:block>
								</fo:table-cell>
							</xsl:for-each>
						</fo:table-row>
					</fo:table-header>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="block">
				<xsl:if test="@placement='footer'">
					<fo:table-footer>
						<fo:table-row>
							<xsl:for-each select="itemData">
								<fo:table-cell padding-start.length="1mm"
									xsl:use-attribute-sets="end-border">
									<fo:block>
										<xsl:value-of select="." />
										&#x00A0;
									</fo:block>
								</fo:table-cell>
							</xsl:for-each>
						</fo:table-row>
					</fo:table-footer>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="block">
				<xsl:if test="@placement='body' and parent::repeatingData">
					<fo:table-body>
						<fo:table-row>
							<xsl:for-each select="itemData">
								<fo:table-cell padding-start.length="1mm"
									xsl:use-attribute-sets="end-border">
									<fo:block>
										<xsl:apply-templates select="." />
										&#x00A0;
									</fo:block>
								</fo:table-cell>
							</xsl:for-each>
						</fo:table-row>
					</fo:table-body>
				</xsl:if>
			</xsl:for-each>
		</fo:table>
	</xsl:template>

	<xsl:template match="itemData">
		<xsl:if test="text()!='' or @class='wording' or @class='image'">
			<xsl:variable name="column1">
				<xsl:choose>
					<xsl:when test="ancestor::repeatingData">
						1
					</xsl:when>
					<xsl:when test="parent::block[@class='address']">
						1
					</xsl:when>
					<xsl:otherwise>
						5
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="column2">
				<xsl:choose>
					<xsl:when test="ancestor::repeatingData">
						29
					</xsl:when>
					<xsl:when test="parent::block[@class='address']">
						31
					</xsl:when>
					<xsl:otherwise>
						30
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="column3">
				<xsl:choose>
					<xsl:when test="ancestor::repeatingData">
						70
					</xsl:when>
					<xsl:when test="parent::block[@class='address']">
						69
					</xsl:when>
					<xsl:otherwise>
						70
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<fo:table xsl:use-attribute-sets="base-table-layout">
				<fo:table-column column-width="proportional-column-width({$column1})" />
				<fo:table-column column-width="proportional-column-width({$column2})" />
				<fo:table-column column-width="proportional-column-width({$column3})" />
				<fo:table-body>
					<xsl:choose>

						<xsl:when test="@class='wording'">
							<xsl:variable name="no-colour">
								<xsl:value-of select="$styles/Styles/Colours/@no-colour" />
							</xsl:variable>
							<xsl:variable name="light-colour">
								<xsl:value-of select="$styles/Styles/Colours/@light-colour" />
							</xsl:variable>
							<xsl:variable name="dark-colour">
								<xsl:value-of select="$styles/Styles/Colours/@dark-colour" />
							</xsl:variable>
							<xsl:variable name="font-colour">
								<xsl:value-of select="$styles/Styles/Colours/@font-colour" />
							</xsl:variable>
							<xsl:variable name="font-header-colour">
								<xsl:value-of select="$styles/Styles/Colours/@font-header" />
							</xsl:variable>

							<xsl:for-each select="p">
								<fo:table-row height="0mm">
									<xsl:copy-of select="$empty-cell" />
									<xsl:copy-of select="$empty-cell" />
								</fo:table-row>
								<fo:table-row keep-with-previous="always">
									<xsl:choose>
										<xsl:when test="@class='term-title'">
											<fo:table-cell
												xsl:use-attribute-sets="before-border after-border start-border end-border">
												<xsl:call-template name="set-term-border">
													<xsl:with-param name="border"
														select="$styles/Styles/Terms/Title/Border[@type='label']" />
												</xsl:call-template>
												<fo:block>&#x00A0;</fo:block>
											</fo:table-cell>
											<fo:table-cell
												xsl:use-attribute-sets="before-border after-border start-border end-border">
												<xsl:call-template name="set-term-border">
													<xsl:with-param name="border"
														select="$styles/Styles/Terms/Title/Border[@type='label']" />
												</xsl:call-template>
												<fo:block xsl:use-attribute-sets="H2">
													<xsl:value-of select="." />
												</fo:block>
											</fo:table-cell>
											<fo:table-cell
												xsl:use-attribute-sets="before-border after-border start-border end-border">
												<xsl:call-template name="set-term-border">
													<xsl:with-param name="border"
														select="$styles/Styles/Terms/Title/Border[@type='body']" />
												</xsl:call-template>
												<fo:block>&#x00A0;</fo:block>
											</fo:table-cell>
										</xsl:when>
										<xsl:otherwise>
											<xsl:copy-of select="$empty-cell" />
											<xsl:copy-of select="$empty-cell" />
										</xsl:otherwise>
									</xsl:choose>
									<xsl:choose>
										<xsl:when test="@class='term-body'">
											<fo:table-cell padding="1mm"
												xsl:use-attribute-sets="before-border after-border start-border end-border">
												<xsl:call-template name="set-term-border">
													<xsl:with-param name="border"
														select="$styles/Styles/Terms/Body/Border" />
												</xsl:call-template>
												<fo:block>
													<xsl:apply-templates select="." />
												</fo:block>
											</fo:table-cell>
										</xsl:when>
										<xsl:otherwise>
											<fo:table-cell
												xsl:use-attribute-sets="before-border after-border start-border end-border">
												<xsl:call-template name="set-term-border">
													<xsl:with-param name="border"
														select="$styles/Styles/Terms/Rest/Border" />
												</xsl:call-template>
												<fo:block>&#x00A0;</fo:block>
											</fo:table-cell>
										</xsl:otherwise>
									</xsl:choose>
								</fo:table-row>
							</xsl:for-each>
						</xsl:when>

						<xsl:when test="@class='line'">
							<xsl:if test="text()!=''">
								<fo:table-row>
									<xsl:copy-of select="$empty-cell" />
									<fo:table-cell>
										<fo:block>
											<xsl:if test="position()=1">
												<xsl:value-of select="../@title" />
											</xsl:if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</xsl:if>
						</xsl:when>

						<xsl:when test="@class='image'">
							<fo:table-row>
								<xsl:copy-of select="$empty-cell" />
								<xsl:copy-of select="$empty-cell" />
								<fo:table-cell>
									<fo:block>
										<xsl:call-template name="insert-image">
											<xsl:with-param name="source" select="@source" />
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:when>

						<xsl:when test="@class='town'">
							<xsl:if test="text()!=''">
								<fo:table-row>
									<xsl:copy-of select="$empty-cell" />
									<xsl:copy-of select="$empty-cell" />
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</xsl:if>
						</xsl:when>

						<xsl:when test="@class='county'">
							<xsl:if test="text()!=''">
								<fo:table-row>
									<xsl:copy-of select="$empty-cell" />
									<xsl:copy-of select="$empty-cell" />
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</xsl:if>
						</xsl:when>

						<xsl:when test="@class='country'">
							<xsl:if test="text()!=''">
								<fo:table-row>
									<xsl:copy-of select="$empty-cell" />
									<xsl:copy-of select="$empty-cell" />
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</xsl:if>
						</xsl:when>

						<xsl:when test="@class='postcode'">
							<xsl:if test="text()!=''">
								<fo:table-row>
									<xsl:copy-of select="$empty-cell" />
									<xsl:copy-of select="$empty-cell" />
									<fo:table-cell>
										<fo:block>
											<xsl:value-of select="." />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</xsl:if>
						</xsl:when>

						<xsl:when test="@class='email'">
							<xsl:variable name="value">
								<xsl:call-template name="email" />
							</xsl:variable>
							<xsl:call-template name="title-variant">
								<xsl:with-param name="data" select="." />
								<xsl:with-param name="value" select="$value" />
							</xsl:call-template>
						</xsl:when>

						<xsl:when test="@class='phone'">
							<xsl:call-template name="title-variant">
								<xsl:with-param name="data" select="." />
								<xsl:with-param name="value" select="." />
							</xsl:call-template>
						</xsl:when>

						<xsl:when test="@class='currency'">
							<xsl:variable name="value">
								<xsl:value-of select="substring-after(.,' ')" />
								<xsl:text>&#x00A0;</xsl:text>
								<xsl:value-of select="substring-before(.,' ')" />
							</xsl:variable>
							<xsl:call-template name="title-variant">
								<xsl:with-param name="data" select="." />
								<xsl:with-param name="value" select="$value" />
							</xsl:call-template>
						</xsl:when>

						<xsl:otherwise>
							<xsl:call-template name="title-variant">
								<xsl:with-param name="data" select="." />
								<xsl:with-param name="value" select="." />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</fo:table-body>
			</fo:table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="set-term-border">
		<xsl:param name="border" />
		<xsl:if test="$border/@before='1'">
			<xsl:attribute name="border-before-width">0.1mm</xsl:attribute>
		</xsl:if>
		<xsl:if test="$border/@after=1">
			<xsl:attribute name="border-after-width">0.1mm</xsl:attribute>
		</xsl:if>
		<xsl:if test="$border/@start=1">
			<xsl:attribute name="border-start-width">0.1mm</xsl:attribute>
		</xsl:if>
		<xsl:if test="$border/@end=1">
			<xsl:attribute name="border-end-width">0.1mm</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="title-variant">
		<xsl:param name="data" select="." />
		<xsl:param name="value" select="''" />

		<fo:table-row line-height="2.0">
			<xsl:choose>
				<xsl:when test="not($data/@title)">
					<fo:table-cell number-columns-spanned="3">
						<fo:block>
							<xsl:value-of select="$value" />
						</fo:block>
					</fo:table-cell>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$empty-cell" />
					<fo:table-cell>
						<fo:block>
							<xsl:value-of select="$data/@title" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<xsl:value-of select="$value" />
						</fo:block>
					</fo:table-cell>
				</xsl:otherwise>
			</xsl:choose>
		</fo:table-row>
	</xsl:template>
</xsl:stylesheet>
