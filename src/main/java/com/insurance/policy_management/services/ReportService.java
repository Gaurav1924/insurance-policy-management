    package com.insurance.policy_management.services;

    import com.insurance.policy_management.exceptions.ResourceNotFoundException;
    import com.insurance.policy_management.model.Policy;
    import com.insurance.policy_management.repository.PolicyRepository;
    import org.redisson.api.RLock;
    import org.redisson.api.RedissonClient;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.stereotype.Service;

    import java.util.Date;
    import java.util.List;
    import java.util.Map;
    import java.util.concurrent.TimeUnit;
    import java.util.stream.Collectors;

    @Service
    public class ReportService {

        @Autowired
        private PolicyRepository policyRepository;

        @Autowired
        private RedissonClient redissonClient;

        @Cacheable(value = "activePolicies")
        public List<Policy> generateActivePoliciesReport() {
            RLock lock = redissonClient.getLock("activePoliciesLock");
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    Date today = new Date();
                    return policyRepository.findAll().stream()
                            .filter(policy -> policy.getStartDate().before(today) && policy.getEndDate().after(today))
                            .collect(Collectors.toList());
                } else {
                    throw new IllegalStateException("Unable to acquire Redis lock to generate active policies report");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
            } finally {
                lock.unlock();
            }
        }

        @Cacheable(value = "expiredPolicies")
        public List<Policy> generateExpiredPoliciesReport() {
            RLock lock = redissonClient.getLock("expiredPoliciesLock");
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    Date today = new Date();
                    return policyRepository.findAll().stream()
                            .filter(policy -> policy.getEndDate().before(today))
                            .collect(Collectors.toList());
                } else {
                    throw new IllegalStateException("Unable to acquire Redis lock to generate expired policies report");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
            } finally {
                lock.unlock();
            }
        }

        @Cacheable(value = "summaryByCustomer")
        public Map<String, List<Policy>> generatePoliciesSummaryByCustomer() {
            RLock lock = redissonClient.getLock("summaryByCustomerLock");
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    return policyRepository.findAll().stream()
                            .collect(Collectors.groupingBy(policy -> policy.getCustomer().getName()));
                } else {
                    throw new IllegalStateException("Unable to acquire Redis lock to generate policies summary by customer");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
            } finally {
                lock.unlock();
            }
        }

        @Cacheable(value = "policiesByType", key = "#type")
        public Map<String, List<Policy>> generatePoliciesByTypeReport(String type) {
            RLock lock = redissonClient.getLock("policiesByTypeLock:" + type);
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    return policyRepository.findAll().stream()
                            .filter(policy -> policy.getType().equalsIgnoreCase(type))
                            .collect(Collectors.groupingBy(Policy::getType));
                } else {
                    throw new IllegalStateException("Unable to acquire Redis lock to generate policies by type report");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
            } finally {
                lock.unlock();
            }
        }

        @Cacheable(value = "policiesByDateRange", key = "{#startDate, #endDate}")
        public List<Policy> getPoliciesByDateRange(Date startDate, Date endDate) {
            RLock lock = redissonClient.getLock("policiesByDateRangeLock");
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    List<Policy> policies = policyRepository.findByDateRange(startDate, endDate);
                    if (policies.isEmpty()) {
                        throw new ResourceNotFoundException("No policies found for the given date range");
                    }
                    return policies;
                } else {
                    throw new IllegalStateException("Unable to acquire Redis lock to fetch policies by date range");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
            } finally {
                lock.unlock();
            }
        }
    }
