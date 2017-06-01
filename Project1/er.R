y1<-c(62.47128731,
      125.3883222,
      259.1950865,
      515.9116164,
      1039.96509,
      1963.321854)
y2<-c(62.73654576,
      121.0912359,
      250.3682662,
      493.8153715,
      1006.38248,
      1991.54363)
y3<-c(52.0465814,
      119.8440544,
      246.6729267,
      480.7844448,
      912.6221273,
      1879.205813)

y4<-c(43.54603204,
      85.88416846,
      156.9709218,
      320.072336,
      698.7917324,
      1379.227287)
x<-c(1,
     2,
     4,
     8,
     16,
     32)




plot(x,y1,main="Throughput Average(kbps)",xlab="packet size(K bytes)",ylab="tput value(kbps)",pch=19)
legend("topleft",pch=c(19,25,8,21),legend=c("delay=0","delay=1","delay=10","delay=100"),x.intersp=0.1,y.intersp = 0.3)
points(x,y2,main="Throughput Average(kbps)",ylim=c(40,2000),pch=25)
points(x,y3,main="Throughput Average(kbps)",ylim=c(40,2000),pch=8)
points(x,y4,main="Throughput Average(kbps)",ylim=c(40,2000),pch=21)

lines(x,y1,main="Throughput Average(kbps)",ylim=c(40,2000))
lines(x,y2,main="Throughput Average(kbps)",ylim=c(40,2000))
lines(x,y3,main="Throughput Average(kbps)",ylim=c(40,2000))
lines(x,y4,main="Throughput Average(kbps)",ylim=c(40,2000))