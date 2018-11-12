% Input Database Info
% Example: DBInfo=[]
% DBInfo.dbname = 'table_name';
% DBInfo.username = 'username';
% DBInfo.password = 'password';
% DBInfo.driver = 'com.mysql.jdbc.Driver';
% DBInfo.dburl = ['URL' dbname];

function competition_analysis(DBInfo)
    conn = database(DBInfo.dbname, DBInfo.username, DBInfo.password, DBInfo.driver, DBInfo.dburl);

    query = 'select cuisine,zipcode from yelp_res.res_cuisine_zipcode group by cuisine,zipcode having count(*)>2';
    curs = exec(conn,query);
    curs = fetch(curs);
    cuisine_zipcodes=curs.data;

    currFolderName = pwd;


    for i=1:size(cuisine_zipcodes,1)
        cuisine = cell2mat(cuisine_zipcodes(i,1));
        zipcode = cell2mat(cuisine_zipcodes(i,2));

        folder_name = strcat('Competition-',cuisine,'-',zipcode);
        mkdir([folder_name, currFolderName]);

        %plot the competition analysis
        query = 'select distinct restaurantID from yelp_res.res_cuisine_zipcode where cuisine=''';
        query = strcat(query, cuisine,''' and zipcode=''',zipcode,'''');
        query1 = strcat(query,' order by restaurantID');
        curs = exec(conn,query1);
        curs = fetch(curs);
        restaurants=curs.data;

        %find the common period for competition
        query1 = strcat('select min(start_date), max(end_date) from yelp_res.zzz_restaurant_months where restaurantID in (',query,')');
        curs = exec(conn,query1);
        curs = fetch(curs);
        dates=curs.data;
        max_date=cell2mat(dates(1));
        min_date=cell2mat(dates(2));

        %find the max period
        query2 = strcat('select max(period) from yelp_res.san_review_period where restaurantID in (',query,')');
        curs = exec(conn,query2);
        curs = fetch(curs);
        period_length=cell2mat(curs.data(1));

        %get all the restaurant data
        [A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_restaurant_data (period_length, restaurants');

        %slide to the same window
        query1 = 'select restaurantID, period_diff(date_format(min(start_date),''%Y%m''), date_format(str_to_date(''01/2004'', ''%m/%Y''),''%Y%m'')) as period';
        query2 = strcat(query1,' from yelp_res.zzz_restaurant_months where restaurantID in (',query,') group by restaurantID  order by restaurantID');
        curs = exec(conn,query2);
        curs = fetch(curs);
        periods = cell2mat(curs.data(:,2));
        periods=periods-min(periods);

        B1 = zeros(size(A1));
        B2 = zeros(size(A1));
        B3 = zeros(size(A1));
        B4 = zeros(size(A1));
        B5 = zeros(size(A1));
        B6 = zeros(size(A1));
        B7 = zeros(size(A1));
        B8 = zeros(size(A1));
        B9 = zeros(size(A1));
        B10 = zeros(size(A1));
        B11 = zeros(size(A1));
        B12 = zeros(size(A1));

        for m=1:size(A1,1)
            B1(m,periods(m)+1:size(A1,2)) = A1(m,1:size(A1,2)-periods(m));
            B2(m,periods(m)+1:size(A1,2)) = A2(m,1:size(A1,2)-periods(m));
            B3(m,periods(m)+1:size(A1,2)) = A3(m,1:size(A1,2)-periods(m));
            B4(m,periods(m)+1:size(A1,2)) = A4(m,1:size(A1,2)-periods(m));
            B5(m,periods(m)+1:size(A1,2)) = A5(m,1:size(A1,2)-periods(m));
            B6(m,periods(m)+1:size(A1,2)) = A6(m,1:size(A1,2)-periods(m));
            B7(m,periods(m)+1:size(A1,2)) = A7(m,1:size(A1,2)-periods(m));
            B8(m,periods(m)+1:size(A1,2)) = A8(m,1:size(A1,2)-periods(m));
            B9(m,periods(m)+1:size(A1,2)) = A9(m,1:size(A1,2)-periods(m));
            B10(m,periods(m)+1:size(A1,2)) = A10(m,1:size(A1,2)-periods(m));
            B11(m,periods(m)+1:size(A1,2)) = A11(m,1:size(A1,2)-periods(m));
            B12(m,periods(m)+1:size(A1,2)) = A12(m,1:size(A1,2)-periods(m));
        end;


        %generate the plots
        name = strcat(folder_name,'/');
        t1=strcat(name,'Rating Not Fake Dislike');
        t2=strcat(name,'Rating Fake Dislike');
        t3=strcat(name,'Rating Not Fake Like');
        t4=strcat(name,'Rating Fake Like');
        t5=strcat(name,'Rating Non Fake Like and Unlike');
        t6=strcat(name,'Rating Fake Like and Unlike');
        t7=strcat(name,'Count Not Fake Dislike');
        t8=strcat(name,'Count Fake Dislike');
        t9=strcat(name,'Count Not Fake Like');
        t10=strcat(name,'Count Fake Like');
        t11=strcat(name,'Count Non Fake Like and Unlike');
        t12=strcat(name,'Count Fake Like and Unlike');

        plot_competition(t1, restaurants, B1);
        plot_competition(t2, restaurants, B2);
        plot_competition(t3, restaurants, B3);
        plot_competition(t4, restaurants, B4);
        plot_competition(t5, restaurants, B5);
        plot_competition(t6, restaurants, B6);
        plot_competition(t7, restaurants, B7);
        plot_competition(t8, restaurants, B8);
        plot_competition(t9, restaurants, B9);
        plot_competition(t10, restaurants, B10);
        plot_competition(t11, restaurants, B11);
        plot_competition(t12, restaurants, B12);
    end;

return;
