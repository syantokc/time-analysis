function imminent_prediction(cluster_number, period_length)
    [restaurants, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_data_weekly (period_length);

    [ksc4, cent4] = ksc_toy(A4, cluster_number);
    t = strcat('Restaurant Like Filtered -- Weeks- ', num2str(period_length),' Cluster - ',num2str(cluster_number));
    plot_graph(ksc4, cent4, cluster_number, t, R_count(:,4),0);

    %lag = [60,110,5];
    lag = [360, 480, 240];
    %lag = [180, 360, 60];

    number_of_interval = 4;
    interval = 50;
    initial=50;


    lag_number=2;
    %period = max(lag)+300;
    period = period_length*3;
    %{
    MSE1 = zeros(number_of_interval-1,lag_number);
    MAE1 = zeros(number_of_interval-1,lag_number);
    DirACC1 = zeros(number_of_interval-1,lag_number);

    MSE2 = zeros(number_of_interval-1,lag_number);
    MAE2 = zeros(number_of_interval-1,lag_number);
    DirACC2 = zeros(number_of_interval-1,lag_number);

    MSE3 = zeros(number_of_interval-1,lag_number);
    MAE3 = zeros(number_of_interval-1,lag_number);
    DirACC3 = zeros(number_of_interval-1,lag_number);

    k1=0;
    k2=0;
    k3=0;
    %}
    %working_set = [49,58,62,18,11,33,44,48,38];
    %working_set = [49,58,62,18,11,33,44,10,51,7,69,61,21,23];
    for i=1:size(restaurants,2)
    %for i = working_set
        restaurantID = restaurants(1,i);
        restaurant_name = get_restaurant_name(restaurantID);
        restaurantID = char(restaurantID);
        disp(strcat(num2str(i),': ', restaurant_name));
        k = ksc4(i);
        YY = get_var_features_normal(restaurantID,period,'N');

        Y2 = YY(lag(k)+1:number_of_interval*interval+initial+lag(k),:);
        %{
        for jj = 3:24
            draw_single_ccf_for_var(i,Y2(:,jj),Y2(:,1), 20, restaurant_name, num2str(jj), 'Popularity');
            draw_single_ccf_for_var(i,Y2(:,jj),Y2(:,2), 20, restaurant_name, num2str(jj), 'Rating');
        end;
        %}
        %Y2 = YY(lag(k)+1:end,:);
        %Y1 = log2(cumsum(cumsum(Y2))+4);
        %Y1 = diff(Y1);
        Y1=cumsum(Y2);
        [a, c] = var_for_single_restaurant_imminent(i, initial, Y1, number_of_interval, interval, k, lag_number, restaurant_name);
        %{
        if k == 1
            MSE1 = MSE1+a;
            MAE1 = MAE1+b;
            DirACC1 = DirACC1+c;
            k1 = k1+1;
        else
            if k == 2
                MSE2 = MSE2+a;
                MAE2 = MAE2+b;
                DirACC2 = DirACC2+c;
                k2=k2+1;
            else
                MSE3 = MSE3+a;
                MAE3 = MAE3+b;
                DirACC3 = DirACC3+c;
                k3=k3+1;
            end;
        end;
        %}
    end;

return;
