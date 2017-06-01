#Graph Generator for CS655 PA2  -Rexwang

#Initialization:
setwd("~/Google Drive/courses/Networks/PHW2/Graph Generator")
rm(list = ls())
#Include packages:
library(ggplot2)
library(lattice)
library(reshape)

#Read in Data:
raw_sr_co0<-read.csv("sr_corrupt=0/sr_corrupt=0data.csv")
raw_sr_lo0<-read.csv("sr_loss=0/sr_loss=0data.csv")
raw_gbn_co0<-read.csv("gbn_corrupt=0/gbn_corrupt=0data.csv")
raw_gbn_lo0<-read.csv("gbn_loss=0/gbn_loss=0data.csv")
raw_sr_3d<-read.csv("3d/sr-3D.csv")
raw_gbn_3d<-read.csv("3d/gbn-3d.csv")
#=====Statistics--SR vs. GBN=====:

#1. Throughput SR vs. GBN under Corruption:
tempdata1<-data.frame(raw_sr_lo0$corrupt,raw_sr_lo0$tput,raw_sr_lo0$tput_ci)
tempdata11<-data.frame(raw_gbn_lo0$corrupt,raw_gbn_lo0$tput,raw_gbn_lo0$tput_ci)
colnames(tempdata1)<-c("corruption_rate","throughput","confidenece_interval")
colnames(tempdata11)<-c("corruption_rate","throughput","confidenece_interval")
g1<-ggplot(tempdata1,aes(x=tempdata1$corruption_rate))
  #Add Confidence Interval SR:
g1<-g1+geom_ribbon(aes(ymin=tempdata1$throughput-tempdata1$confidenece_interval,ymax=tempdata1$throughput+tempdata1$confidenece_interval),linetype="blank",color='skyblue',fill='skyblue')
g1<-g1+geom_line(aes(y=tempdata1$throughput,color="blue"))+labs(title="Throughput SR vs. GBN under Corruption with Confidence Interval",y="Throughput",x="Corruption")
  #Add Confidence Interval GBN:
g1<-g1+geom_ribbon(aes(ymin=tempdata11$throughput-tempdata11$confidenece_interval,ymax=tempdata11$throughput+tempdata11$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g1<-g1+geom_line(aes(y=tempdata11$throughput,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('blue'='blue','red'='red'), labels = c('SR','GBN'))

ggsave("Throughput SR vs. GBN under Corruption.png",g1,width=12, height=6.5,dpi=300)

#2. Goodput SR vs. GBN under Corruption:
tempdata2<-data.frame(raw_sr_lo0$corrupt,raw_sr_lo0$goodput,raw_sr_lo0$goodput_ci)
tempdata22<-data.frame(raw_gbn_lo0$corrupt,raw_gbn_lo0$goodput,raw_gbn_lo0$goodput_ci)
colnames(tempdata2)<-c("corruption_rate","goodput","confidenece_interval")
colnames(tempdata22)<-c("corruption_rate","goodput","confidenece_interval")
g2<-ggplot(tempdata2,aes(x=tempdata2$corruption_rate))
  #Add Confidence Interval SR:
g2<-g2+geom_ribbon(aes(ymin=tempdata2$goodput-tempdata2$confidenece_interval,ymax=tempdata2$goodput+tempdata2$confidenece_interval),linetype="blank",color='skyblue',fill='skyblue')
g2<-g2+geom_line(aes(y=tempdata2$goodput,color="blue"))+labs(title="Goodput SR vs. GBN under Corruption with Confidence Interval",y="Goodput",x="Corruption")
  #Add Confidence Interval GBN:
g2<-g2+geom_ribbon(aes(ymin=tempdata22$goodput-tempdata22$confidenece_interval,ymax=tempdata22$goodput+tempdata22$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g2<-g2+geom_line(aes(y=tempdata22$goodput,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('blue'='blue','red'='red'), labels = c('SR','GBN'))

ggsave("Goodput SR vs. GBN under Corruption.png",g2,width=12, height=6.5,dpi=300)

#3. Average packet delay SR vs. GBN under Corruption:
tempdata3<-data.frame(raw_sr_lo0$corrupt,raw_sr_lo0$delay,raw_sr_lo0$delay_ci)
tempdata33<-data.frame(raw_gbn_lo0$corrupt,raw_gbn_lo0$delay,raw_gbn_lo0$delay_ci)
colnames(tempdata3)<-c("corruption_rate","delay","confidenece_interval")
colnames(tempdata33)<-c("corruption_rate","delay","confidenece_interval")
g3<-ggplot(tempdata3,aes(x=tempdata2$corruption_rate))
  #Add Confidence Interval SR:
g3<-g3+geom_ribbon(aes(ymin=tempdata3$delay-tempdata3$confidenece_interval,ymax=tempdata3$delay+tempdata3$confidenece_interval),linetype="blank",color='skyblue',fill='skyblue')
g3<-g3+geom_line(aes(y=tempdata3$delay,color="blue"))+labs(title="Average packet delay SR vs. GBN under Corruption with Confidence Interval",y="Delay",x="Corruption")
  #Add Confidence Interval GBN:
g3<-g3+geom_ribbon(aes(ymin=tempdata33$delay-tempdata33$confidenece_interval,ymax=tempdata33$delay+tempdata33$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g3<-g3+geom_line(aes(y=tempdata33$delay,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('blue'='blue','red'='red'), labels = c('SR','GBN'))

ggsave("Average packet delay SR vs. GBN under Corruption.png",g3,width=12, height=6.5,dpi=300)

#4. Throughput SR vs. GBN under Losses:
tempdata4<-data.frame(raw_sr_co0$loss,raw_sr_co0$tput,raw_sr_co0$tput_ci)
tempdata44<-data.frame(raw_gbn_co0$loss,raw_gbn_co0$tput,raw_gbn_co0$tput_ci)
colnames(tempdata4)<-c("Loss_rate","throughput","confidenece_interval")
colnames(tempdata44)<-c("Loss_rate","throughput","confidenece_interval")
g4<-ggplot(tempdata4,aes(x=tempdata4$Loss_rate))
  #Add Confidence Interval SR:
g4<-g4+geom_ribbon(aes(ymin=tempdata4$throughput-tempdata4$confidenece_interval,ymax=tempdata4$throughput+tempdata4$confidenece_interval),linetype="blank",color='skyblue',fill='skyblue')
g4<-g4+geom_line(aes(y=tempdata4$throughput,color="blue"))+labs(title="Throughput SR vs. GBN under Losses with Confidence Interval",y="Throughput",x="Losses")
  #Add Confidence Interval GBN:
g4<-g4+geom_ribbon(aes(ymin=tempdata44$throughput-tempdata44$confidenece_interval,ymax=tempdata44$throughput+tempdata44$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g4<-g4+geom_line(aes(y=tempdata44$throughput,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('blue'='blue','red'='red'), labels = c('SR','GBN'))

ggsave("Throughput SR vs. GBN under Losses.png",g4,width=12, height=6.5,dpi=300)

#5. Goodput SR vs. GBN under Losses:
tempdata5<-data.frame(raw_sr_co0$loss,raw_sr_co0$goodput,raw_sr_co0$goodput_ci)
tempdata55<-data.frame(raw_gbn_co0$loss,raw_gbn_co0$goodput,raw_gbn_co0$goodput_ci)
colnames(tempdata5)<-c("Loss_rate","goodput","confidenece_interval")
colnames(tempdata55)<-c("Loss_rate","goodput","confidenece_interval")
g5<-ggplot(tempdata5,aes(x=tempdata5$Loss_rate))
  #Add Confidence Interval SR:
g5<-g5+geom_ribbon(aes(ymin=tempdata5$goodput-tempdata5$confidenece_interval,ymax=tempdata5$goodput+tempdata5$confidenece_interval),linetype="blank",color='skyblue',fill='skyblue')
g5<-g5+geom_line(aes(y=tempdata5$goodput,color="blue"))+labs(title="Goodput SR vs. GBN under Losses with Confidence Interval",y="Goodput",x="Losses")
  #Add Confidence Interval GBN:
g5<-g5+geom_ribbon(aes(ymin=tempdata55$goodput-tempdata55$confidenece_interval,ymax=tempdata55$goodput+tempdata55$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g5<-g5+geom_line(aes(y=tempdata55$goodput,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('blue'='blue','red'='red'), labels = c('SR','GBN'))

ggsave("Goodput SR vs. GBN under Losses.png",g5,width=12, height=6.5,dpi=300)

#6. Average packet delay SR vs. GBN under Losses:
tempdata6<-data.frame(raw_sr_co0$loss,raw_sr_co0$delay,raw_sr_co0$delay_ci)
tempdata66<-data.frame(raw_gbn_co0$loss,raw_gbn_co0$delay,raw_gbn_co0$delay_ci)
colnames(tempdata6)<-c("Loss_rate","delay","confidenece_interval")
colnames(tempdata66)<-c("Loss_rate","delay","confidenece_interval")
g6<-ggplot(tempdata6,aes(x=tempdata6$Loss_rate))
  #Add Confidence Interval SR:
g6<-g6+geom_ribbon(aes(ymin=tempdata6$delay-tempdata6$confidenece_interval,ymax=tempdata6$delay+tempdata6$confidenece_interval),linetype="blank",color='skyblue',fill='skyblue')
g6<-g6+geom_line(aes(y=tempdata6$delay,color="blue"))+labs(title="Average packet delay SR vs. GBN under Losses with Confidence Interval",y="Delay",x="Losses")
  #Add Confidence Interval GBN:
g6<-g6+geom_ribbon(aes(ymin=tempdata66$delay-tempdata66$confidenece_interval,ymax=tempdata66$delay+tempdata66$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g6<-g6+geom_line(aes(y=tempdata66$delay,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('blue'='blue','red'='red'), labels = c('SR','GBN'))

ggsave("Average packet delay SR vs. GBN under Losses.png",g6,width=12, height=6.5,dpi=300)

#================================
#=====Statistics--GBN #retransmits=====:
#7. Num of retransmits of GBN as a function of Loss(corruption=0):
tempdata77<-data.frame(raw_gbn_co0$loss,raw_gbn_co0$retransmit,raw_gbn_co0$retransmit_ci)
colnames(tempdata77)<-c("Loss_rate","#retransmit","confidenece_interval")
g7<-ggplot(tempdata77,aes(x=tempdata77$Loss_rate))

g7<-g7+geom_ribbon(aes(ymin=tempdata77$`#retransmit`-tempdata77$confidenece_interval,ymax=tempdata77$`#retransmit`+tempdata77$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g7<-g7+geom_line(aes(y=tempdata77$`#retransmit`,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('red'='red'), labels = c('GBN'))
g7<-g7+labs(title="Num of retransmits of GBN as a function of Loss(corruption=0)",y="Num of retransmits",x="Losses")

ggsave("Num of retransmits of GBN as a function of Loss(corruption=0).png",g7,width=12, height=6.5,dpi=300)

#8. Num of retransmits of GBN as a function of Corruption(Loss=0):
tempdata88<-data.frame(raw_gbn_lo0$corrupt,raw_gbn_lo0$retransmit,raw_gbn_lo0$retransmit_ci)
colnames(tempdata88)<-c("corruption_rate","#retransmit","confidenece_interval")
g8<-ggplot(tempdata88,aes(x=tempdata88$corruption_rate))

g8<-g8+geom_ribbon(aes(ymin=tempdata88$`#retransmit`-tempdata88$confidenece_interval,ymax=tempdata88$`#retransmit`+tempdata88$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g8<-g8+geom_line(aes(y=tempdata88$`#retransmit`,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('red'='red'), labels = c('GBN'))
g8<-g8+labs(title="Num of retransmits of GBN as a function of Corruption(Loss=0)",y="Num of retransmits",x="Corruption")

ggsave("Num of retransmits of GBN as a function of Corruption(Loss=0).png",g8,width=12, height=6.5,dpi=300)

#=====Statistics--GBN Performance=====:
#9.Average time to communicate packet vs.corruption:

tempdata99<-data.frame(raw_gbn_lo0$corrupt,raw_gbn_lo0$performance,raw_gbn_lo0$performance_ci)
colnames(tempdata99)<-c("corruption_rate","Average_time_to_communicate_packet","confidenece_interval")
tempdata99$Average_time_to_communicate_packet[11]=Inf
tempdata99$confidenece_interval[11]=0
g9<-ggplot(tempdata99,aes(x=tempdata99$corruption_rate))

g9<-g9+geom_ribbon(aes(ymin=tempdata99$Average_time_to_communicate_packet-tempdata99$confidenece_interval,ymax=tempdata99$Average_time_to_communicate_packet+tempdata99$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g9<-g9+geom_line(aes(y=tempdata99$Average_time_to_communicate_packet,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('red'='red'), labels = c('GBN'))
g9<-g9+labs(title="Average time to communicate packet vs.corruption(Loss=0)",y="Average_time_to_communicate_packet",x="Corruption")

ggsave("Average time to communicate packet vs.corruption(Loss=0).png",g9,width=12, height=6.5,dpi=300)

#10.Average time to communicate packet vs.loss:

tempdata1010<-data.frame(raw_gbn_co0$loss,raw_gbn_co0$performance,raw_gbn_co0$performance_ci)
colnames(tempdata1010)<-c("Loss_rate","Average_time_to_communicate_packet","confidenece_interval")
tempdata1010$Average_time_to_communicate_packet[11]=Inf
tempdata1010$confidenece_interval[11]=0
g10<-ggplot(tempdata1010,aes(x=tempdata1010$Loss_rate))

g10<-g10+geom_ribbon(aes(ymin=tempdata1010$Average_time_to_communicate_packet-tempdata1010$confidenece_interval,ymax=tempdata1010$Average_time_to_communicate_packet+tempdata1010$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g10<-g10+geom_line(aes(y=tempdata1010$Average_time_to_communicate_packet,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('red'='red'), labels = c('GBN'))
g10<-g10+labs(title="Average time to communicate packet vs.loss(corruption=0)",y="Average_time_to_communicate_packet",x="Losses")

ggsave("Average time to communicate packet vs.loss(corruption=0).png",g10,width=12, height=6.5,dpi=300)

#=====Statistics--GBN RTT=====:

#11.Average RTT as function of loss:

tempdata1111<-data.frame(raw_gbn_co0$loss,raw_gbn_co0$rtt,raw_gbn_co0$rtt_ci)
colnames(tempdata1111)<-c("Loss_rate","RTT","confidenece_interval")
tempdata1111$RTT[5:11]=Inf
tempdata1111$confidenece_interval[5:11]=0
g11<-ggplot(tempdata1111,aes(x=tempdata1111$Loss_rate))

g11<-g11+geom_ribbon(aes(ymin=tempdata1111$RTT-tempdata1111$confidenece_interval,ymax=tempdata1111$RTT+tempdata1111$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g11<-g11+geom_line(aes(y=tempdata1111$RTT,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('red'='red'), labels = c('GBN'))
g11<-g11+labs(title="Average RTT as function of loss(corruption=0)",y="RTT",x="Losses")

ggsave("Average RTT as function of loss(corruption=0).png",g11,width=12, height=6.5,dpi=300)

#12.Average RTT as function of corruption:

tempdata1212<-data.frame(raw_gbn_lo0$corrupt,raw_gbn_lo0$rtt,raw_gbn_lo0$rtt_ci)
colnames(tempdata1212)<-c("corruption_rate","RTT","confidenece_interval")
tempdata1212$RTT[5:11]=Inf
tempdata1212$confidenece_interval[5:11]=0
g12<-ggplot(tempdata1212,aes(x=tempdata1212$corruption_rate))

g12<-g12+geom_ribbon(aes(ymin=tempdata1212$RTT-tempdata1212$confidenece_interval,ymax=tempdata1212$RTT+tempdata1212$confidenece_interval),linetype="blank",color='yellow',fill='yellow')
g12<-g12+geom_line(aes(y=tempdata1212$RTT,color="red"))+scale_fill_identity(guide = 'legend')+scale_colour_manual(name = 'Legend',values =c('red'='red'), labels = c('GBN'))
g12<-g12+labs(title="Average RTT as function of corruption(loss=0)",y="RTT",x="Corruption")

ggsave("Average RTT as function of corruption(loss=0).png",g12,width=12, height=6.5,dpi=300)

#==============Average time to communicate a packet vs LOSS+CORRUPTION (3D)============
wireframe(performance ~ loss * corrupt, data = raw_gbn_3d,
          scales = list(arrows = FALSE),
          drape = TRUE, colorkey = TRUE,
          screen = list(z = 30, x = -60))

wireframe(performance ~ loss * corrupt, data = raw_sr_3d,
          scales = list(arrows = FALSE),
          drape = TRUE, colorkey = TRUE,
          screen = list(z = 30, x = -60))
#==========with Confidence Interval===============
gr1<-c(rep(1,length(raw_gbn_3d$performance)))
raw_gbn_3d1<-data.frame(raw_gbn_3d$loss,raw_gbn_3d$corrupt,raw_gbn_3d$performance,gr1)
colnames(raw_gbn_3d1)<-c("loss","corrupt","performance","gr")
gr2<-2*gr1
raw_gbn_3d2<-data.frame(raw_gbn_3d$loss,raw_gbn_3d$corrupt,raw_gbn_3d$performance+raw_gbn_3d$performance_ci,gr2)
colnames(raw_gbn_3d2)<-c("loss","corrupt","performance","gr")
gr3<-3*gr1
raw_gbn_3d3<-data.frame(raw_gbn_3d$loss,raw_gbn_3d$corrupt,raw_gbn_3d$performance-raw_gbn_3d$performance_ci,gr3)
colnames(raw_gbn_3d3)<-c("loss","corrupt","performance","gr")
raw_gbn_3d4<-rbind(raw_gbn_3d1,raw_gbn_3d2,raw_gbn_3d3)


wireframe(performance ~ loss * corrupt, data = raw_gbn_3d4,groups=gr,
          scales = list(arrows = FALSE),
          drape = TRUE, colorkey = TRUE,
          screen = list(z = 30, x = -60))