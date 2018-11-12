function [xcf,lags, bounds] = get_single_ccf(y,x, lag, restaurant_name, title_name)
    figure('visible','off');
    [xcf,lags, bounds] = crosscorr(y, x, lag);
    crosscorr(y, x, lag);
    title(title_name);
    print(strcat('Restaurant-',restaurant_name, '--', title_name),'-dpng');
return;