package com.ail.eft.bacs;
/**
 * This package contains a utility service to create BACS Electronic File Transfer submission file contents.<br/>
 * Submission files are fixed-width format files which contain 1 or more rows to set up Direct Debits or request payments
 * as well as a number of header and footer rows.<br/>
 * There are a number of POJOs in this package that describe individual rows in files as well as helper classes to put
 * them all together.<br/><br/>
 *
 * See <a href="https://www.bacs.co.uk/Pages/SearchResources.aspx">https://www.bacs.co.uk/Pages/SearchResources.aspx</a>,
 * Especially <a href="https://www.bacs.co.uk/DocumentLibrary/EFT_file_structures.pdf">https://www.bacs.co.uk/DocumentLibrary/EFT_file_structures.pdf</a>
 *
 * <br/><br/>
 * Certain abbreviations are used in the field descriptions of the POJOs:
 * <table>
 *   <thead>
 *     <tr><th>Abbreviation</th><th>Meaning</th></tr>
 *   </thead>
 *   <tbody>
 *     <tr><td>b</td><td>Blank space</td></tr>
 *     <tr><td>n</td><td>Any numeral</td></tr>
 *     <tr><td>sfs</td><td>Single file submission</td></tr>
 *     <tr><td>mfs</td><td>Multifile submission</td></tr>
 *     <tr><td>spd</td><td>Single processing day payment file</td></tr>
 *     <tr><td>mpd</td><td>Multiprocessing day payment file</td></tr>
 *     <tr><td>sub</td><td>Submission (this abbreviation is used in action columns in tables)</td></tr>
 *     <tr><td>R</td><td>Right justified. This is used in the field column to show that the contents of the field must be
 *                       right justified. That is, the data must end at the right and any unused places must be zeros.</td></tr>
 *     <tr><td>L</td><td>Left justified. This is used in the field column to show that the contents of the field must be
 *                       left justified. That is, the data must start at the left and any unused places must be blank filled.</td></tr>
 *     <tr><td>P</td><td>Used in the field column of output records to indicate the contents of the field can be determined by the receiving party.</td></tr>
 *     <tr><td>V</td><td>Used in the field column of output records to indicate the contents are variable and therefore can change (although the validation does not).</td></tr>
 *     <tr><td>DDD</td><td>The Julian date</td></tr>
 *     <tr><td></td><td></td></tr>
 *   </tbody>
 */