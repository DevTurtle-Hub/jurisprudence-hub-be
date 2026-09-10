package jurisprudence_hub_be.common.audit;

import jurisprudence_hub_be.common.security.util.SecurityUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @SuppressWarnings("null")
    public Optional<String> getCurrentAuditor() {
        return SecurityUtil.getCurrentUserId();
    }
}
