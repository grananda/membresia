package services.contract;

import java.util.List;

public interface StatisticServiceInterface {
    /**
     * Returns a list is total money collected per month period and subscription
     *
     * @param token Subscription token
     * @return List
     */
    List getTotalPaymentsBySubscription(String token);

    /**
     * Gets a list of the most recent registered payment
     *
     * @return List
     */
    List getLatestPayments();

    /**
     * Returns the sum of all registered payments per month period
     *
     * @return List
     */
    List getTotalPaymentsByPeriod();

    /**
     * Returns a list of all installment per current month period
     *
     * @return List
     */
    List getInstallmentsByPeriod();
}
