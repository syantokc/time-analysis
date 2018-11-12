function [A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_restaurant_data (period_length, restaurants, DBInfo)
    conn = database(DBInfo.dbname, DBInfo.username, DBInfo.password, DBInfo.driver, DBInfo.dburl);
    
    A1 = zeros(length(restaurants),period_length);
    A2 = zeros(length(restaurants),period_length);
    A3 = zeros(length(restaurants),period_length);
    A4 = zeros(length(restaurants),period_length);
    A5 = zeros(length(restaurants),period_length);
    A6 = zeros(length(restaurants),period_length);
    A7 = zeros(length(restaurants),period_length);
    A8 = zeros(length(restaurants),period_length);
    A9 = zeros(length(restaurants),period_length);
    A10 = zeros(length(restaurants),period_length);
    A11 = zeros(length(restaurants),period_length);
    A12 = zeros(length(restaurants),period_length);
    R_count = zeros(length(restaurants),6);

    ct_index = 1;
    for restaurant = restaurants
        restaurantID = restaurant(1);
        %period_length = cell2mat(restaurant(2))+1;


        res_1 = [];
        res_2 = [];
        res_3 = [];
        res_4 = [];
        res_5 = [];
        res_6 = [];

        query_base = strcat('select period, sum(rating) as rating_sum, sum(review_count) as review_count from yelp_res.san_review_period where restaurantID = ''',char(restaurantID), '''');
        query_base_N = strcat('select period, sum(6*review_count-rating) as rating_sum, sum(review_count) as review_count from yelp_res.san_review_period where restaurantID = ''',char(restaurantID), '''');
        %rating_type = 0 and unfiltered rating flagged = 'N'
        query1 = strcat(query_base_N, ' and rating_type = 0 and flagged = ''N'' group by period order by 1');
        curs = exec(conn,query1);
        curs = fetch(curs);
        if rows(curs) > 0
            res_1 = cell2mat(curs.data)';
        end

        %rating_type = 0 and filtered rating flagged = 'Y'
        query2 = strcat(query_base_N, ' and rating_type = 0 and flagged = ''Y'' group by period order by 1');
        curs = exec(conn,query2);
        curs = fetch(curs);
        if rows(curs) > 0
            res_2 = cell2mat(curs.data)';
        end

        %rating_type = 1 and unfiltered rating flagged = 'N'
        query3 = strcat(query_base, ' and rating_type = 1 and flagged = ''N'' group by period order by 1');
        curs = exec(conn,query3);
        curs = fetch(curs);
        if rows(curs) > 0
            res_3 = cell2mat(curs.data)';
        end

        %rating_type = 1 and filtered rating flagged = 'Y'
        query4 = strcat(query_base, ' and rating_type = 1 and flagged = ''Y'' group by period order by 1');
        curs = exec(conn,query4);
        curs = fetch(curs);
        if rows(curs) > 0
            res_4 = cell2mat(curs.data)';
        end


        %rating_type = both 0 and 1 and rating flagged = 'N'
        query5 = strcat(query_base, ' and flagged = ''N''  group by period order by 1');
        curs = exec(conn,query5);
        curs = fetch(curs);
        if rows(curs) > 0
            res_5 = cell2mat(curs.data)';
        end

        %rating_type = both 0 and 1 and rating flagged = 'Y'
        query6 = strcat(query_base, ' and flagged = ''Y'' group by period order by 1');
        curs = exec(conn,query6);
        curs = fetch(curs);
        if rows(curs) > 0
            res_6 = cell2mat(curs.data)';
        end

        temp = 0 * ones(1, 6);
        count= 0 * ones(1, 6);
        review_count = 0 * ones(1, 6);

        Y1 = 0 * ones(1, period_length);
        Y2 = 0 * ones(1, period_length);
        Y3 = 0 * ones(1, period_length);
        Y4 = 0 * ones(1, period_length);
        Y5 = 0 * ones(1, period_length);
        Y6 = 0 * ones(1, period_length);
        Y7 = 0 * ones(1, period_length);
        Y8 = 0 * ones(1, period_length);
        Y9 = 0 * ones(1, period_length);
        Y10 = 0 * ones(1, period_length);
        Y11 = 0 * ones(1, period_length);
        Y12 = 0 * ones(1, period_length);

        for i = 0:period_length-1

            %check the no. of months the data is avaialable
            t1 = size(res_1);
            t2 = size(res_2);
            t3 = size(res_3);
            t4 = size(res_4);
            t5 = size(res_5);
            t6 = size(res_6);

            if count(1) < t1(2)
                if res_1(count(1)*3+1) == i
                    temp(1) = temp(1) + res_1(count(1)*3+2);
                    review_count(1) = review_count(1) + res_1(count(1)*3+3);
                    count(1) = count(1) + 1;
                end
            end
            if count(2) < t2(2)
                if res_2(count(2)*3+1) == i
                    temp(2) = temp(2) + res_2(count(2)*3+2);
                    review_count(2) = review_count(2) + res_2(count(2)*3+3);
                    count(2) = count(2) + 1;
                end
            end
            if count(3) < t3(2)
                if res_3(count(3)*3+1) == i
                    temp(3) = temp(3) + res_3(count(3)*3+2);
                    review_count(3) = review_count(3) + res_3(count(3)*3+3);
                    count(3) = count(3) + 1;
                end
            end
            if count(4) < t4(2)
                if res_4(count(4)*3+1) == i
                    temp(4) = temp(4) + res_4(count(4)*3+2);
                    review_count(4) = review_count(4) + res_4(count(4)*3+3);
                    count(4) = count(4) + 1;
                end
            end
            if count(5) < t5(2)
                if res_5(count(5)*3+1) == i
                    temp(5) = temp(5) + res_5(count(5)*3+2);
                    review_count(5) = review_count(5) + res_5(count(5)*3+3);
                    count(5) = count(5) + 1;
                end
            end
            if count(6) < t6(2)
                if res_6(count(6)*3+1) == i
                    temp(6) = temp(6) + res_6(count(6)*3+2);
                    review_count(6) = review_count(6) + res_6(count(6)*3+3);
                    count(6) = count(6) + 1;
                end
            end
            Y1(i+1) = temp(1)/max(review_count(1),1);
            Y2(i+1) = temp(2)/max(review_count(2),1);
            Y3(i+1) = temp(3)/max(review_count(3),1);
            Y4(i+1) = temp(4)/max(review_count(4),1);
            Y5(i+1) = temp(5)/max(review_count(5),1);
            Y6(i+1) = temp(6)/max(review_count(6),1);
            Y7(i+1) = review_count(1);
            Y8(i+1) = review_count(2);
            Y9(i+1) = review_count(3);
            Y10(i+1) = review_count(4);
            Y11(i+1) = review_count(5);
            Y12(i+1) = review_count(6);
        end

        A1(ct_index,:) = Y1;
        A2(ct_index,:) = Y2;
        A3(ct_index,:) = Y3;
        A4(ct_index,:) = Y4;
        A5(ct_index,:) = Y5;
        A6(ct_index,:) = Y6;
        A7(ct_index,:) = Y7;
        A8(ct_index,:) = Y8;
        A9(ct_index,:) = Y9;
        A10(ct_index,:) = Y10;
        A11(ct_index,:) = Y11;
        A12(ct_index,:) = Y12;
        R_count(ct_index,:) = review_count;

        ct_index = ct_index + 1;
    end
    clear javaclasspath;
    return;
