% Input Database Info
% Example: DBInfo=[]
% DBInfo.dbname = 'table_name';
% DBInfo.username = 'username';
% DBInfo.password = 'password';
% DBInfo.driver = 'com.mysql.jdbc.Driver';
% DBInfo.dburl = ['URL' dbname];

function [restaurants, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_data_weekly (period_length, DBInfo)
    conn = database(DBInfo.dbname, DBInfo.username, DBInfo.password, DBInfo.driver, DBInfo.dburl);

    query_i = 'select restaurantID from yelp_res.san_review_period_weekly group by restaurantID having max(period) >= ';
    query_i = strcat(query_i,num2str(period_length-1));
    query = strcat('select restaurantID from yelp_res.san_review_period_weekly where period <= ',num2str(period_length-1),' and restaurantID in (');
    query = strcat(query, query_i, ') group by restaurantID having count(distinct flagged, rating_type) = 4');
    curs = exec(conn,query);
    curs = fetch(curs);
    restaurants = curs.data;
    restaurants = restaurants';

    [A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_restaurant_data_weekly (period_length, restaurants);

return;
