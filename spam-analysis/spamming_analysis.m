function spamming_analysis(cluster_number, period_length)
    javaclasspath('./mysql-connector-java-5.1.36/mysql-connector-java-5.1.36-bin.jar');
    [restaurants, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_data (period_length);

    %generate clusters
    %generate plot for the clusters
    [ksc2, cent2] = ksc_toy(A2, cluster_number);
    t = strcat('Restaurant time series-- Months- ', num2str(period_length),' id- A2 - dislike-filtered - Cluster - ',num2str(cluster_number));
    plot_graph(cent2, cluster_number, t);
    cluster_analysis(cluster_number, period_length, restaurants, ksc8,'Cluster A2: ');

return;
