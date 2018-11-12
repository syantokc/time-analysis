function buffer_action(period_length)
    [restaurants, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R_count] = read_data_weekly (period_length);

    for i=1:size(restaurants,2)
        restaurantID = cellstr(restaurants(i));
        [restaurant_name] = get_restaurant_name(restaurantID);
        x = 1:period_length;

        y1 = A5(i,:)/5;
        y2 = A6(i,:)/5;
        y3 = A4(i,:)/5;
        b1 = get_slope(A12(i,:));
        b2 = get_slope(A10(i,:));


        opt = [];
        opt.XLabel = 'Number of Months';   % xlabel
        opt.BoxDim = [1, 2.5];
        opt.FontSize = 9;
        opt.XLim = [0 period_length];
        opt.YLim = [0 1];
        opt.XTick = [0:period_length/10]*10;


        figure('visible','off');
        [hAx,hLine1,hLine2] = plotyy(x,y1,x,b1);
        hLine1.Color = 'g';
        hLine2.Color = 'b';
        hold on;
        plot(x,y2,'r');
        legend('Average Truthful Rating', 'Average Deceptive Rating','Slope of Deceptive Count','Location','northoutside','Orientation','horizontal');

        title(restaurant_name);
        xlabel('No of months');
        ylabel(hAx(1),'Rating'); % left y-axis
        ylabel(hAx(2),'Slope of Deceptive Count');
        print(strcat('Buffer-Restaurant-',restaurant_name ,'-Deceptive Average'),'-dpng');

        figure('visible','off');
        [hAx,hLine1,hLine2] = plotyy(x,y1,x,b2);
        %hLine1.LineStyle = '--';
        hLine1.Color = 'g';
        hLine2.Color = 'b';
        hold on;
        plot(x,y3,'r');
        legend('Average Truthful Rating', 'Average Deceptive Positive Rating','Slope of Deceptive Count','Location','northoutside','Orientation','horizontal');


        title(restaurant_name);
        xlabel('No of months');

        ylabel(hAx(1),'Rating'); % left y-axis
        ylabel(hAx(2),'Slope of Deceptive Positive Count');
        print(strcat('Buffer-Restaurant-', restaurant_name ,'-Deceptive Positive'),'-dpng');

    end;

return;
