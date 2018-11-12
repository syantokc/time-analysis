function cluster_analysis(cluster_number, period_length, restaurants, ksc, prefix)
    for i = 1: cluster_number
        res = [];
        for j = 1:length(restaurants)
            if i == ksc(j)
                res = [res;restaurants(j)];
            end;
        end;

        if size(res,1) > 0
            [A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_restaurant_data (period_length, res');

            [ksc1, cent1] = ksc_toy(A1, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A1 - Dislike Non-Fake - Cluster - ',num2str(cluster_number));
            plot_graph(ksc1, cent1, cluster_number, t, R_count(:,1),0);

            [ksc2, cent2] = ksc_toy(A2, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A2 - Dislike Fake - Cluster - ',num2str(cluster_number));
            plot_graph(ksc2, cent2, cluster_number, t, R_count(:,2),0);

            [ksc3, cent3] = ksc_toy(A3, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A3 - Like Non-fake - Cluster - ',num2str(cluster_number));
            plot_graph(ksc3, cent3, cluster_number, t, R_count(:,3),0);

            [ksc4, cent4] = ksc_toy(A4, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A4 - Like Fake - Cluster - ',num2str(cluster_number));
            plot_graph(ksc4, cent4, cluster_number, t, R_count(:,4),0);

            [ksc5, cent5] = ksc_toy(A5, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A5 - Overall Non-fake - Cluster - ',num2str(cluster_number));
            plot_graph(ksc5, cent5, cluster_number, t, R_count(:,5),0);

            [ksc6, cent6] = ksc_toy(A6, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A6 - Overall Fake - Cluster - ',num2str(cluster_number));
            plot_graph(ksc6, cent6, cluster_number, t, R_count(:,6),0);

            [ksc7, cent7] = ksc_toy(A7, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A7 - Dislike Fake Count - Cluster - ',num2str(cluster_number));
            plot_graph(ksc7, cent7, cluster_number, t, R_count(:,1),1);

            [ksc8, cent8] = ksc_toy(A8, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A8 - Dislike Non-fake Count - Cluster - ',num2str(cluster_number));
            plot_graph(ksc8, cent8, cluster_number, t, R_count(:,2),1);

            [ksc9, cent9] = ksc_toy(A9, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A9 - Like Non-fake Count - Cluster - ',num2str(cluster_number));
            plot_graph(ksc9, cent9, cluster_number, t, R_count(:,3),1);

            [ksc10, cent10] = ksc_toy(A10, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A10 - Like Fake Count - Cluster - ',num2str(cluster_number));
            plot_graph(ksc10, cent10, cluster_number, t, R_count(:,4),1);

            [ksc11, cent11] = ksc_toy(A11, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A11 - Overall Non-fake Count - Cluster - ',num2str(cluster_number));
            plot_graph(ksc11, cent11, cluster_number, t, R_count(:,5),1);

            [ksc12, cent12] = ksc_toy(A12, cluster_number);
            t = strcat(prefix,'Restaurant -- Cluster ', num2str(i),' Months-',num2str(period_length),' id- A12 - Overall Fake Count - Cluster - ',num2str(cluster_number));
            plot_graph(ksc12, cent12, cluster_number, t, R_count(:,6),1);
        end;

    end;
return;
