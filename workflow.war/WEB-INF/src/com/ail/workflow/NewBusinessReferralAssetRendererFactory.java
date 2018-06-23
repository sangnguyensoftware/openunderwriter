package com.ail.workflow;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.persistence.hibernate.HibernateRunInTransaction;
import com.ail.insurance.policy.Policy;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRendererFactory;

public class NewBusinessReferralAssetRendererFactory extends BaseAssetRendererFactory {


    @Override
    public AssetRenderer getAssetRenderer(long classPK, int type) throws PortalException, SystemException {

        try {
            CoreContext.initialise();

            return new HibernateRunInTransaction<NewBusinessReferralAssetRenderer>() {
                private Long classPK;

                public HibernateRunInTransaction<NewBusinessReferralAssetRenderer> with(Long classPK) {
                    this.classPK = classPK;
                    return this;
                }

                @Override
                public NewBusinessReferralAssetRenderer run() throws Throwable {
                    CoreProxy proxy = new CoreProxy();
                    Policy policy = (Policy) proxy.queryUnique("get.policy.by.systemId", classPK);

                    return new NewBusinessReferralAssetRenderer(new NewBusinessReferralAssetRendererData(policy));
                }
            }.with(classPK).invoke().result();
        } catch (PortalException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch(Throwable t) {
            throw new PortalException(t);
        }
        finally {
            CoreContext.destroy();
        }
    }

    @Override
    public String getClassName() {
        return NewBusinessReferral.class.getName();
    }

    @Override
    public String getType() {
        return "NewBusinessReferral";
    }
}
