function ccf_plot(period_length, lag)
    [restaurants, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_data_weekly (period_length);
    res_count = size(restaurants,2);

    for i=1:res_count
        restaurantID = cellstr(restaurants(i));
        [restaurant_name] = get_restaurant_name(restaurantID);

        y1 = A5(i,:)/5; %avg truthful rating
        y2 = A4(i,:)/5; %avg deceptive like rating
        y3 = get_slope(A10(i,:)); %slope--# of deceptive like review

        x1 = A5(i,:)/5; %avg truthful rating
        x2 = A1(i,:)/5; %avg truthful dislike rating
        x3 = A3(i,:)/5; %avg truthful like rating
        x4 = A4(i,:)/5; %avg deceptive like rating
        x5 = A6(i,:)/5; %avg deceptive rating


        x6 = get_slope(A10(i,:)); %slope--# of deceptive like review
        x7 = get_slope(A11(i,:)); %slope--# of truthful review
        x8 = get_slope(A9(i,:)); %slope--# of truthful like review
        x9 = get_slope(A7(i,:)); %slope--# of truthful dislike like review

        [xcf1,lags1, bounds1] = get_single_ccf(x4,y1, lag, restaurant_name, 'Rating Truthful Avg vs Rating Deceptive Like');
        [xcf2,lags2, bounds2] = get_single_ccf(x5,y1, lag, restaurant_name, 'Rating Truthful Avg vs Rating Deceptive Avg');
        [xcf3,lags3, bounds3] = get_single_ccf(x6,y1, lag, restaurant_name, 'Rating Truthful Avg vs Slope Deceptive Like Count');

        [xcf4,lags4, bounds4] = get_single_ccf(y2,x2, lag, restaurant_name, 'Rating Deceptive Like vs Rating Truthful Dislike');
        [xcf5,lags5, bounds5] = get_single_ccf(y2,x3, lag, restaurant_name, 'Rating Deceptive Like vs Rating Truthful Like');
        [xcf6,lags6, bounds6] = get_single_ccf(y2,x7, lag, restaurant_name, 'Rating Deceptive Like vs Slope Truthful Count');
        [xcf7,lags7, bounds7] = get_single_ccf(y2,x8, lag, restaurant_name, 'Rating Deceptive Like vs Slope Truthful Like Count');
        [xcf8,lags8, bounds8] = get_single_ccf(y2,x9, lag, restaurant_name, 'Rating Deceptive Like vs Slope Truthful Dislike Count');


        [xcf9,lags9, bounds9] = get_single_ccf(y3,x2, lag, restaurant_name, 'Slope Deceptive Like vs Rating Truthful Dislike');
        [xcf10,lags10, bounds10] = get_single_ccf(y3,x3, lag, restaurant_name, 'Slope Deceptive Like vs Rating Truthful Like');
        [xcf11,lags11, bounds11] = get_single_ccf(y3,x7, lag, restaurant_name, 'Slope Deceptive Like vs Slope Truthful Count');
        [xcf12,lags12, bounds12] = get_single_ccf(y3,x8, lag, restaurant_name, 'Slope Deceptive Like vs Slope Truthful Like Count');
        [xcf13,lags13, bounds13] = get_single_ccf(y3,x9, lag, restaurant_name, 'Slope Deceptive Like vs Slope Truthful Dislike Count');

    end;

    y1 = sum(A5)/res_count/5; %avg truthful rating
    y2 = sum(A4)/res_count/5; %avg deceptive like rating
    y3 = get_slope(sum(A10)); %slope--# of deceptive like review

    x1 = sum(A5)/res_count/5; %avg truthful rating
    x2 = sum(A1)/res_count/5; %avg truthful dislike rating
    x3 = sum(A3)/res_count/5; %avg truthful like rating
    x4 = sum(A4)/res_count/5; %avg deceptive like rating
    x5 = sum(A6)/res_count/5; %avg deceptive rating


    x6 = get_slope(sum(A10)); %slope--# of deceptive like review
    x7 = get_slope(sum(A11)); %slope--# of truthful review
    x8 = get_slope(sum(A9)); %slope--# of truthful like review
    x9 = get_slope(sum(A7)); %slope--# of truthful dislike like review

    restaurant_name = 'Overall';
    [xcf1,lags1, bounds1] = get_single_ccf(y1,x4, lag, restaurant_name, 'Rating Truthful Avg vs Rating Deceptive Like');
    [xcf2,lags2, bounds2] = get_single_ccf(y1,x5, lag, restaurant_name, 'Rating Truthful Avg vs Rating Deceptive Avg');
    [xcf3,lags3, bounds3] = get_single_ccf(y1,x6, lag, restaurant_name, 'Rating Truthful Avg vs Slope Deceptive Like Count');

    [xcf4,lags4, bounds4] = get_single_ccf(y2,x2, lag, restaurant_name, 'Rating Deceptive Like vs Rating Truthful Dislike');
    [xcf5,lags5, bounds5] = get_single_ccf(y2,x3, lag, restaurant_name, 'Rating Deceptive Like vs Rating Truthful Like');
    [xcf6,lags6, bounds6] = get_single_ccf(y2,x7, lag, restaurant_name, 'Rating Deceptive Like vs Slope Truthful Count');
    [xcf7,lags7, bounds7] = get_single_ccf(y2,x8, lag, restaurant_name, 'Rating Deceptive Like vs Slope Truthful Like Count');
    [xcf8,lags8, bounds8] = get_single_ccf(y2,x9, lag, restaurant_name, 'Rating Deceptive Like vs Slope Truthful Dislike Count');


    [xcf9,lags9, bounds9] = get_single_ccf(y3,x2, lag, restaurant_name, 'Slope Deceptive Like vs Rating Truthful Dislike');
    [xcf10,lags10, bounds10] = get_single_ccf(y3,x3, lag, restaurant_name, 'Slope Deceptive Like vs Rating Truthful Like');
    [xcf11,lags11, bounds11] = get_single_ccf(y3,x7, lag, restaurant_name, 'Slope Deceptive Like vs Slope Truthful Count');
    [xcf12,lags12, bounds12] = get_single_ccf(y3,x8, lag, restaurant_name, 'Slope Deceptive Like vs Slope Truthful Like Count');
    [xcf13,lags13, bounds13] = get_single_ccf(y3,x9, lag, restaurant_name, 'Slope Deceptive Like vs Slope Truthful Dislike Count');

return;
