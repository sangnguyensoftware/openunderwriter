/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.pageflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ail.core.Type;
import com.ail.insurance.policy.Clause;
import com.ail.insurance.policy.Policy;

public class ClauseDetails extends PageElement {
	private static final long serialVersionUID = -4810599045554021748L;
	private String groupBy;

	public ClauseDetails() {
		super();
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public Map<String, List<Clause>> getGroupedClauses(Policy policy) {
        Map<String,List<Clause>> groupedClauses=new HashMap<String,List<Clause>>();
        
        for(Clause clause: policy.getClause()) {
            StringBuffer compoundKey=new StringBuffer();

            if (groupBy!=null) {
                for(Iterator<String> it=clause.xpathIterate(groupBy, String.class) ; it.hasNext() ; ) {
                    String gp=it.next();
                    if (compoundKey.length()==0) {
                        if (!it.hasNext()) {
                            compoundKey.append(String.format(i18n("i18n_clause_details_apply_to_only"), gp));
                        }
                        else {
                            compoundKey.append(String.format(i18n("i18n_clause_details_apply_to"), gp));
                        }
                    }
                    else if (!it.hasNext()) {
                        compoundKey.append(i18n("i18n_clause_details_last_separator")).append(gp);
                    }
                    else {
                        compoundKey.append(i18n("i18n_clause_details_separator")).append(gp);
                    }
                }
            }
            else {
                compoundKey.append(i18n("i18n_clause_details_applicable"));
            }
            
            String key=compoundKey.toString();
            
            if (!groupedClauses.containsKey(key)) {
                groupedClauses.put(key, new ArrayList<Clause>());
            }
            
            groupedClauses.get(key).add(clause);
        }

        return groupedClauses;
	}
	
	@Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
	    return executeTemplateCommand("ClauseDetails", model);
	}
}
